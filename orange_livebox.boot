firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    name WAN_IN {
        default-action drop
        description "packets from Internet to LAN"
        enable-default-log
        rule 1 {
            action accept
            description "allow established sessions"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 2 {
            action drop
            description "drop invalid state"
            log disable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "packets from Internet to the router"
        rule 1 {
            action accept
            description "allow established session to the router"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 2 {
            action drop
            description "drop invalid state"
            log disable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
    }
    options {
        mss-clamp {
            mss 1452
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    bridge br0 {

    }
    ethernet eth0 {
        address 192.168.1.1/24
        description LAN1
        duplex auto
        speed auto
    }
    ethernet eth1 {
        description Internet
        duplex auto
        speed auto
        vif 835 {
            address dhcp
            description FTTH
            pppoe 0 {
                default-route auto
                firewall {
                    in {
                        name WAN_IN
                    }
                    local {
                        name WAN_LOCAL
                    }
                }
                mtu 1492
                name-server auto
                user-id fti/user
                password secret
            }
        }
        vif 838 {
            bridge-group {
                bridge br0
            }
            description TV
        }
        vif 840 {
            bridge-group {
                bridge br0
            }
            description TV
            
        }
        vif 851 {
            bridge-group {
                bridge br0
            }
            description VoIP
        }
    }
    ethernet eth2 {
        address 192.168.2.1/24
        description LAN2
        duplex auto
        speed auto
        vif 835 {
        }
        vif 838 {
            bridge-group {
                bridge br0
            }
            description TV
        }
        vif 840 {
            bridge-group {
                bridge br0
            }
            description TV
        }
        vif 851 {
            bridge-group {
                bridge br0
            }
            description VoIP
        }
    }
    loopback lo {
    }
}

service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name LAN1 {
            authoritative disable
            subnet 192.168.1.0/24 {
                default-router 192.168.1.1
                dns-server 192.168.1.1
                lease 86400
                start 192.168.1.2 {
                    stop 192.168.1.200
                }
            }
        }
        shared-network-name LAN2 {
            authoritative enable
            subnet 192.168.2.0/24 {
                default-router 192.168.2.1
                dns-server 192.168.2.1
                lease 86400
                start 192.168.2.21 {
                    stop 192.168.2.200
                }
            }
        }
    }
    dns {
        forwarding {
            cache-size 1000
            listen-on eth2
            listen-on eth0
        }
    }
    gui {
        https-port 443
    }
    mdns {
        reflector
    }
    nat {
        rule 5010 {
            outbound-interface pppoe0
            type masquerade
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
    upnp2 {
        listen-on eth0
        listen-on eth2
        nat-pmp enable
        secure-mode disable
        wan pppoe0
    }
    pppoe-server {
        dns-servers {
            server-1 80.10.246.2
            server-2 80.10.246.129
        }
        authentication {
            local-users {
                username fti/user {
                    password secret
            }
        }
        mode local
        }
        client-ip-pool {
            start 192.168.2.210
            stop 192.168.2.220
        }
        interface eth2.835
        mtu 1492
    }
}
system {
    config-management {
        commit-revisions 50
    }
    host-name ubnt
    login { 
        user ubnt {
            authentication {
                encrypted-password $1$zKNoUbAo$gomzUbYvgyUMcD436Wo66.
            }
            level admin
        }
    }
    name-server 8.8.8.8
    name-server 8.8.4.4
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    offload {
        ipv4 {
            forwarding enable
            pppoe enable
            vlan enable
        }
    }
    package {
        repository wheezy {
            components "main contrib non-free"
            distribution wheezy
            password ""
            url http://http.us.debian.org/debian
            username ""
        }
        repository wheezy-security {
            components main
            distribution wheezy/updates
            password ""
            url http://security.debian.org
            username ""
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level warning
            }
        }
    }
    time-zone Europe/Paris
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@4:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.6.0beta1.4705702.140925.2253 */