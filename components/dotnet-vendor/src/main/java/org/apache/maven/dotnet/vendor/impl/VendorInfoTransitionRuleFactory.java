/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.dotnet.vendor.impl;

import org.apache.maven.dotnet.vendor.*;
import org.apache.maven.dotnet.InitializationException;
import org.apache.maven.dotnet.PlatformUnsupportedException;
import org.apache.maven.dotnet.registry.RepositoryRegistry;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.io.File;

import org.codehaus.plexus.logging.Logger;

/**
 * Provides factory methods for creating vendor info transition rules. These rules usually can determine the
 * exact vendor info; but at times, it is a best guess.
 *
 * @author Shane Isbell
 * @see VendorInfoState
 */
final class VendorInfoTransitionRuleFactory
{

    /**
     * A registry component of repository (config) files
     */
    private RepositoryRegistry repositoryRegistry;

    private SettingsRepository settingsRepository;

    private VendorInfoRepository vendorInfoRepository;

    /**
     * The default vendor as specified within the nmaven-settings file
     */
    private Vendor defaultVendor;

    /**
     * The default vendor version as specified within the nmaven-settings file
     */
    private String defaultVendorVersion;

    /**
     * The default framework version as specified within the nmaven-settings file
     */
    private String defaultFrameworkVersion;

    private List<VendorInfo> vendorInfos;

    /**
     * A logger for writing log messages
     */
    private Logger logger;

    /**
     * Default constructor
     */
    VendorInfoTransitionRuleFactory()
    {
    }

    /**
     * Initializes this factory.
     *
     * @param repositoryRegistry   the repository registry containing various NMaven config information.
     * @param vendorInfoRepository the vendor info repository used for accessing the nmaven-settings config file
     * @param logger               the plexus logger
     * @throws InitializationException if there is a problem initializing this factory
     */
    void init( RepositoryRegistry repositoryRegistry, VendorInfoRepository vendorInfoRepository, Logger logger )
        throws InitializationException
    {
        this.repositoryRegistry = repositoryRegistry;
        this.vendorInfoRepository = vendorInfoRepository;
        this.logger = logger;
        if ( repositoryRegistry == null )
        {
            throw new InitializationException( "NMAVEN-103-000: Unable to find the repository registry" );
        }

        settingsRepository = (SettingsRepository) repositoryRegistry.find( "nmaven-settings" );
        if ( settingsRepository == null )
        {
            throw new InitializationException(
                "NMAVEN-103-001: Settings Repository is null. Aborting initialization of VendorInfoTranstionRuleFactory" );

        }

        try
        {
            defaultVendor = VendorFactory.createVendorFromName( settingsRepository.getDefaultSetup().getVendorName() );
        }
        catch ( VendorUnsupportedException e )
        {
            throw new InitializationException( "NMAVEN-103-002: Unknown Default Vendor: Name = " + defaultVendor );
        }
        defaultVendorVersion = settingsRepository.getDefaultSetup().getVendorVersion().trim();
        defaultFrameworkVersion = settingsRepository.getDefaultSetup().getFrameworkVersion().trim();
        vendorInfos = settingsRepository.getVendorInfos();
    }

    /**
     * Returns the vendor info transition rule for state: Vendor is Novell, vendor version exists, framework version exists.
     *
     * @return the vendor info transition rule for state: Vendor is Novell, vendor version exists, framework version exists.
     */
    VendorInfoTransitionRule createVendorInfoSetterForNTT()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-003: Entering State = NTT" );
                return VendorInfoState.EXIT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForNFF()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-004: Entering State = NFF" );
                if ( vendorInfo.getVendor().equals( defaultVendor ) )
                {
                    vendorInfo.setVendorVersion( defaultVendorVersion );
                    vendorInfo.setFrameworkVersion( defaultFrameworkVersion );
                    return VendorInfoState.EXIT;
                }
                else
                {
                    List<VendorInfo> v = vendorInfoRepository.getVendorInfosFor( vendorInfo, true );
                    if ( !v.isEmpty() )
                    {
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getVendor().equals( vendorInfo.getVendor() ) )
                            {
                                vendorInfo.setVendorVersion( vi.getVendorVersion() );
                                vendorInfo.setFrameworkVersion( "2.0.50727" );
                                return VendorInfoState.EXIT;
                            }
                        }
                    }
                    else
                    {
                        v = vendorInfoRepository.getVendorInfosFor( vendorInfo, false );
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getVendor().equals( vendorInfo.getVendor() ) )
                            {
                                vendorInfo.setVendorVersion( vi.getVendorVersion() );
                                vendorInfo.setFrameworkVersion(
                                    "2.0.50727" );  //TODO: this should be according to max version
                                return VendorInfoState.EXIT;
                            }
                        }
                    }
                }
                return createVendorInfoSetterForNFF_NoSettings().process( vendorInfo );
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForNFF_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-005: Entering State = NFF" );
                vendorInfo.setFrameworkVersion( "2.0.50727" );
                return VendorInfoState.NFT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForNFT_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-006: Entering State = NFT" );
                return VendorInfoState.EXIT; //NO WAY TO KNOW
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForNFT()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-007: Entering State = NFT" );
                if ( vendorInfo.getFrameworkVersion().equals( defaultFrameworkVersion ) )
                {
                    vendorInfo.setVendorVersion( defaultVendorVersion );
                    vendorInfo.setVendor( defaultVendor );
                    return VendorInfoState.NTT;
                }
                else
                {
                    List<VendorInfo> v = vendorInfoRepository.getVendorInfosFor( vendorInfo, true );
                    if ( !v.isEmpty() )
                    {
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getFrameworkVersion().equals( vendorInfo.getFrameworkVersion() ) )
                            {
                                vendorInfo.setVendorVersion( vi.getVendorVersion() );
                                vendorInfo.setVendor( vi.getVendor() );
                                return VendorInfoState.NTT;
                            }
                        }
                        return createVendorInfoSetterForNFT_NoSettings().process( vendorInfo );
                    }
                    else
                    {
                        v = vendorInfoRepository.getVendorInfosFor( vendorInfo, false );
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getFrameworkVersion().equals( vendorInfo.getFrameworkVersion() ) )
                            {
                                vendorInfo.setVendorVersion( vi.getVendorVersion() );
                                vendorInfo.setVendor( vi.getVendor() );
                                return VendorInfoState.NTT;
                            }
                        }
                        return createVendorInfoSetterForNFT_NoSettings().process( vendorInfo );
                    }
                }
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForNTF_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-008: Entering State = NTF" );
                vendorInfo.setFrameworkVersion( "2.0.50727" );
                return VendorInfoState.NTT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForNTF()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-009: Entering State = NTF" );
                if ( vendorInfo.getVendorVersion().equals( defaultVendorVersion ) )
                {
                    vendorInfo.setFrameworkVersion( defaultFrameworkVersion );
                    vendorInfo.setVendor( defaultVendor );
                    return VendorInfoState.NTT;
                }
                else
                {
                    List<VendorInfo> v = vendorInfoRepository.getVendorInfosFor( vendorInfo, true );
                    if ( !v.isEmpty() )
                    {
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getVendorVersion().equals( vendorInfo.getVendorVersion() ) )
                            {
                                vendorInfo.setFrameworkVersion( vi.getFrameworkVersion() );
                                vendorInfo.setVendor( vi.getVendor() );
                                return VendorInfoState.NTT;
                            }
                        }
                        return createVendorInfoSetterForNTF_NoSettings().process( vendorInfo );
                    }
                    else
                    {
                        v = vendorInfoRepository.getVendorInfosFor( vendorInfo, false );
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getVendorVersion().equals( vendorInfo.getVendorVersion() ) )
                            {
                                vendorInfo.setFrameworkVersion( vi.getFrameworkVersion() );
                                vendorInfo.setVendor( vi.getVendor() );
                                return VendorInfoState.NTT;
                            }
                        }
                        return createVendorInfoSetterForNTF_NoSettings().process( vendorInfo );
                    }
                }
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForFTF_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-010: Entering State = FTF" );
                String vendorVersion = vendorInfo.getVendorVersion();
                if ( vendorVersion.equals( "2.0.50727" ) || vendorVersion.equals( "1.1.4322" ) )
                {
                    vendorInfo.setVendor( Vendor.MICROSOFT );
                    return VendorInfoState.MTF;
                }
                else
                {
                    vendorInfo.setVendor( Vendor.MONO );//This could be dotGNU: this is best guess
                    return VendorInfoState.NTF;
                }
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForFTF()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-011: Entering State = FTF" );
                if ( vendorInfo.getVendorVersion().equals( defaultVendorVersion ) )
                {
                    vendorInfo.setFrameworkVersion( defaultFrameworkVersion );
                    vendorInfo.setVendor( defaultVendor );
                    if ( defaultVendor.equals( Vendor.MICROSOFT ) )
                    {
                        return VendorInfoState.MTT;
                    }
                    else if ( defaultVendor.equals( Vendor.MONO ) )
                    {
                        return VendorInfoState.NTT;
                    }
                    else
                    {
                        return VendorInfoState.GTT;
                    }
                }
                else
                {
                    List<VendorInfo> v = vendorInfoRepository.getVendorInfosFor( vendorInfo, true );
                    if ( !v.isEmpty() )
                    {
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getVendorVersion().equals( vendorInfo.getVendorVersion() ) )
                            {
                                vendorInfo.setFrameworkVersion( vi.getFrameworkVersion() );
                                vendorInfo.setVendor( vi.getVendor() );
                                if ( vi.getVendor().equals( Vendor.MICROSOFT ) )
                                {
                                    return VendorInfoState.MTT;
                                }
                                else if ( vi.getVendor().equals( Vendor.MONO ) )
                                {
                                    return VendorInfoState.NTT;
                                }
                                else
                                {
                                    return VendorInfoState.GTT;
                                }
                            }
                        }
                        return createVendorInfoSetterForFTF_NoSettings().process( vendorInfo );
                    }
                    else
                    {
                        v = vendorInfoRepository.getVendorInfosFor( vendorInfo, false );
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getVendorVersion().equals( vendorInfo.getVendorVersion() ) )
                            {
                                vendorInfo.setFrameworkVersion( vi.getFrameworkVersion() );
                                vendorInfo.setVendor( vi.getVendor() );
                                if ( vi.getVendor().equals( Vendor.MICROSOFT ) )
                                {
                                    return VendorInfoState.MTT;
                                }
                                else if ( vi.getVendor().equals( Vendor.MONO ) )
                                {
                                    return VendorInfoState.NTT;
                                }
                                else
                                {
                                    return VendorInfoState.GTT;
                                }
                            }
                        }
                        return createVendorInfoSetterForFTF_NoSettings().process( vendorInfo );
                    }
                }
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForFFT()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-012: Entering State = FFT" );
                if ( vendorInfo.getFrameworkVersion().equals( defaultFrameworkVersion ) )
                {
                    vendorInfo.setVendorVersion( defaultVendorVersion );
                    vendorInfo.setVendor( defaultVendor );
                    if ( defaultVendor.equals( Vendor.MICROSOFT ) )
                    {
                        return VendorInfoState.MTT;
                    }
                    else if ( defaultVendor.equals( Vendor.MONO ) )
                    {
                        return VendorInfoState.NTT;
                    }
                    else
                    {
                        return VendorInfoState.GTT;
                    }
                }
                else
                {
                    List<VendorInfo> v = vendorInfoRepository.getVendorInfosFor( vendorInfo, true );
                    if ( !v.isEmpty() )
                    {
                        for ( VendorInfo vi : v )
                        {
                            if ( vi.getFrameworkVersion().equals( vendorInfo.getFrameworkVersion() ) )
                            {
                                vendorInfo.setVendorVersion( vi.getVendorVersion() );
                                vendorInfo.setVendor( vi.getVendor() );
                                if ( vi.getVendor().equals( Vendor.MICROSOFT ) )
                                {
                                    return VendorInfoState.MTT;
                                }
                                else if ( vi.getVendor().equals( Vendor.MONO ) )
                                {
                                    return VendorInfoState.NTT;
                                }
                                else
                                {
                                    return VendorInfoState.GTT;
                                }
                            }
                        }
                    }
                    v = vendorInfoRepository.getVendorInfosFor( vendorInfo, false );
                    for ( VendorInfo vi : v )
                    {
                        if ( vi.getFrameworkVersion().equals( vendorInfo.getFrameworkVersion() ) )
                        {
                            vendorInfo.setVendorVersion( vi.getVendorVersion() );
                            vendorInfo.setVendor( vi.getVendor() );
                            if ( vi.getVendor().equals( Vendor.MICROSOFT ) )
                            {
                                return VendorInfoState.MTT;
                            }
                            else if ( vi.getVendor().equals( Vendor.MONO ) )
                            {
                                return VendorInfoState.NTT;
                            }
                            else
                            {
                                return VendorInfoState.GTT;
                            }
                        }
                    }
                    return createVendorInfoSetterForFFT_NoSettings().process( vendorInfo );

                }
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForFFT_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-013: Entering State = FFT" );
                try
                {
                    vendorInfo.setVendor( VendorFactory.getDefaultVendorForOS() );
                }
                catch ( PlatformUnsupportedException e )
                {
                    return VendorInfoState.EXIT;
                }
                return ( vendorInfo.getVendor().equals( Vendor.MICROSOFT ) ) ? VendorInfoState.MFT
                    : VendorInfoState.NFT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForFTT_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-014: Entering State = FTT" );
                String vendorVersion = vendorInfo.getVendorVersion();
                Vendor defaultVendor;
                try
                {
                    defaultVendor = VendorFactory.getDefaultVendorForOS();
                }
                catch ( PlatformUnsupportedException e )
                {
                    return VendorInfoState.EXIT;
                }
                if ( ( vendorVersion.equals( "2.0.50727" ) || vendorVersion.equals( "1.1.4322" ) ) &&
                    defaultVendor.equals( Vendor.MICROSOFT ) )
                {
                    vendorInfo.setVendor( Vendor.MICROSOFT );
                    return VendorInfoState.MTT;
                }
                else
                {
                    vendorInfo.setVendor( Vendor.MONO );//This could be dotGNU: this is best guess
                    return VendorInfoState.NTT;
                }
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForFTT()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-015: Entering State = FTT" );
                List<VendorInfo> vendorInfos = vendorInfoRepository.getVendorInfosFor( vendorInfo, false );
                if ( vendorInfos.isEmpty() )
                {
                    return createVendorInfoSetterForFTT_NoSettings().process( vendorInfo );
                }
                Vendor vendor = vendorInfos.get( 0 ).getVendor();//TODO: Do default branch
                vendorInfo.setVendor( vendor );
                if ( vendor.equals( Vendor.MICROSOFT ) )
                {
                    return VendorInfoState.MTT;
                }
                else if ( vendor.equals( Vendor.MONO ) )
                {
                    return VendorInfoState.NTT;
                }
                else
                {
                    return VendorInfoState.GTT;
                }
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForFFF_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-016: Entering State = FFF" );
                try
                {
                    vendorInfo.setVendor( VendorFactory.getDefaultVendorForOS() );
                }
                catch ( PlatformUnsupportedException e )
                {
                    return VendorInfoState.EXIT;
                }
                return ( vendorInfo.getVendor().equals( Vendor.MICROSOFT ) ) ? VendorInfoState.MFF
                    : VendorInfoState.NFF;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForFFF()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-017: Entering State = FFF" );
                vendorInfo.setVendor( defaultVendor );
                vendorInfo.setVendorVersion( defaultVendorVersion );
                vendorInfo.setFrameworkVersion( defaultFrameworkVersion );
                return VendorInfoState.EXIT;
            }
        };
    }


    VendorInfoTransitionRule createVendorInfoSetterForMTT()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-018: Entering State = MTT" );
                return VendorInfoState.EXIT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForMTF()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-019: Entering State = MTF" );
                vendorInfo.setFrameworkVersion( vendorInfo.getVendorVersion() );
                return VendorInfoState.MTT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForMFT()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-020: Entering State = MTF" );
                vendorInfo.setVendorVersion( vendorInfo.getFrameworkVersion() );
                return VendorInfoState.MTT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForMFF_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-021: Entering State = MFF" );
                File v1 = new File( "C:\\WINDOWS\\Microsoft.NET\\Framework\\v1.1.4322" );
                File v2 = new File( "C:\\WINDOWS\\Microsoft.NET\\Framework\\v2.0.50727" );
                if ( v2.exists() )
                {
                    vendorInfo.setFrameworkVersion( "2.0.50727" );
                    vendorInfo.setExecutablePath( v2 );
                }
                else if ( v1.exists() )
                {
                    vendorInfo.setFrameworkVersion( "1.1.4322" );
                    vendorInfo.setExecutablePath( v1 );
                }
                else
                {
                    vendorInfo.setFrameworkVersion( "2.0.50727" );
                }
                return VendorInfoState.MFT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForMFF()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-022: Entering State = MFF" );
                if ( vendorInfo.getVendor().equals( defaultVendor ) )
                {
                    vendorInfo.setVendorVersion( defaultVendorVersion );
                    return VendorInfoState.MTF;
                }
                else
                {
                    List<VendorInfo> vendorInfos = vendorInfoRepository.getVendorInfosFor( vendorInfo, false );
                    Set<String> versions = new HashSet<String>();
                    for ( VendorInfo vi : vendorInfos )
                    {
                        String frameworkVersion = vi.getFrameworkVersion();
                        String vendorVersion = vi.getVendorVersion();
                        if ( frameworkVersion != null )
                        {
                            versions.add( frameworkVersion );
                        }
                        if ( vendorVersion != null )
                        {
                            versions.add( vi.getVendorVersion() );
                        }
                    }
                    try
                    {
                        String maxVersion = vendorInfoRepository.getMaxVersion( versions );
                        vendorInfo.setVendorVersion( maxVersion );
                        return VendorInfoState.MTF;
                    }
                    catch ( InvalidVersionFormatException e )
                    {
                        logger.info( "NMAVEN-103-030: Invalid version. Unable to determine best vendor version", e );
                        return createVendorInfoSetterForMFF_NoSettings().process( vendorInfo );
                    }
                }
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForGFF_NoSettings()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-023: Entering State = GFF" );
                vendorInfo.setFrameworkVersion( "2.0.50727" );
                vendorInfo.setVendorVersion( "2.0.50727" );
                return VendorInfoState.EXIT;
            }
        };
    }

    VendorInfoTransitionRule createVendorInfoSetterForGFF()
    {
        return new VendorInfoTransitionRule()
        {
            public VendorInfoState process( VendorInfo vendorInfo )
            {
                logger.debug( "NMAVEN-103-023: Entering State = GFF" );
                if ( vendorInfo.getVendor().equals( defaultVendor ) )
                {
                    vendorInfo.setVendorVersion( defaultVendorVersion );
                    vendorInfo.setFrameworkVersion( "2.0.50727" );
                    return VendorInfoState.EXIT;
                }
                else
                {
                    List<VendorInfo> vendorInfos = vendorInfoRepository.getVendorInfosFor( vendorInfo, false );
                    Set<String> versions = new HashSet<String>();
                    for ( VendorInfo vi : vendorInfos )
                    {
                        String vendorVersion = vi.getVendorVersion();
                        if ( vendorVersion != null )
                        {
                            versions.add( vi.getVendorVersion() );
                        }
                    }
                    try
                    {
                        String maxVersion = vendorInfoRepository.getMaxVersion( versions );
                        vendorInfo.setVendorVersion( maxVersion );
                        vendorInfo.setFrameworkVersion( "2.0.50727" );
                        return VendorInfoState.EXIT;
                    }
                    catch ( InvalidVersionFormatException e )
                    {
                        logger.info( "NMAVEN-103-030: Invalid version. Unable to determine best vendor version", e );
                        return createVendorInfoSetterForGFF_NoSettings().process( vendorInfo );
                    }
                }
            }
        };
    }
}