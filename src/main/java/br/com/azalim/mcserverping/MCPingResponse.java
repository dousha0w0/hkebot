package br.com.azalim.mcserverping;

/*
 * Copyright 2014 jamietech. All rights reserved.
 * https://github.com/jamietech/MinecraftServerPing
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */
import java.util.List;

/**
 * References: http://wiki.vg/Server_List_Ping
 * <a href="https://gist.github.com/thinkofdeath/6927216">...</a>
 */
public class MCPingResponse {
    /**
     */
    private Description description;
    /**
     */
    private Players players;
    /**
     */
    private Version version;
    /**
     */
    private String favicon;
    /**
     */
    private long ping;
    /**
     */
    private String hostname;
    /**
     */
    private long port;


    public class Description {
        /**
         */
        private String text;

        public String getStrippedText() {
            return MCPingUtil.stripColors(this.text);
        }

        //<editor-fold defaultstate="collapsed" desc="delombok">
        /**
         * @return Server description text
         */
        @SuppressWarnings("all")
        public String getText() {
            return this.text;
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "MCPingResponse.Description(text=" + this.getText() + ")";
        }
        //</editor-fold>
    }


    public class Players {
        //<editor-fold defaultstate="collapsed" desc="delombok">
        /**
         */
        private int max;
        /**
         */
        private int online;
        /**
         */
        private List<Player> sample;
        //</editor-fold>

        /**
         * @return Maximum player count
         */
        @SuppressWarnings("all")
        public int getMax() {
            return this.max;
        }

        /**
         * @return Online player count
         */
        @SuppressWarnings("all")
        public int getOnline() {
            return this.online;
        }

        /**
         * @return List of some players (if any) specified by server
         */
        @SuppressWarnings("all")
        public List<Player> getSample() {
            return this.sample;
        }

        //<editor-fold defaultstate="collapsed" desc="delombok">
        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "MCPingResponse.Players(max=" + this.getMax() + ", online=" + this.getOnline() + ", sample=" + this.getSample() + ")";
        }
        //</editor-fold>
    }


    public class Player {
        //<editor-fold defaultstate="collapsed" desc="delombok">
        /**
         */
        private String name;
        /**
         */
        private String id;
        //</editor-fold>

        /**
         * @return Name of player
         */
        @SuppressWarnings("all")
        public String getName() {
            return this.name;
        }

        /**
         * @return Unknown
         */
        @SuppressWarnings("all")
        public String getId() {
            return this.id;
        }

        //<editor-fold defaultstate="collapsed" desc="delombok">
        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "MCPingResponse.Player(name=" + this.getName() + ", id=" + this.getId() + ")";
        }
        //</editor-fold>
    }


    //<editor-fold defaultstate="collapsed" desc="delombok">
    public class Version {
    //</editor-fold>
        /**
         * @return Version name (ex: 13w41a)
         */
        private String name;
        /**
         * @return Protocol version
         */
        private int protocol;

        //<editor-fold defaultstate="collapsed" desc="delombok">
        @SuppressWarnings("all")
        public String getName() {
            return this.name;
        }

        @SuppressWarnings("all")
        public int getProtocol() {
            return this.protocol;
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "MCPingResponse.Version(name=" + this.getName() + ", protocol=" + this.getProtocol() + ")";
        }
        //</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="delombok">
    /**
     * @return the MOTD
     */
    @SuppressWarnings("all")
    public Description getDescription() {
        return this.description;
    }

    /**
     * @return @{link Players}
     */
    @SuppressWarnings("all")
    public Players getPlayers() {
        return this.players;
    }

    /**
     * @return @{link Version}
     */
    @SuppressWarnings("all")
    public Version getVersion() {
        return this.version;
    }

    /**
     * @return Base64 encoded favicon image
     */
    @SuppressWarnings("all")
    public String getFavicon() {
        return this.favicon;
    }

    /**
     * @return Ping in ms.
     */
    @SuppressWarnings("all")
    public long getPing() {
        return this.ping;
    }

    /**
     * @return Host name, will show real IP if using SRV record
     */
    @SuppressWarnings("all")
    public String getHostname() {
        return this.hostname;
    }

    /**
     * @return Port, will show real port if using SRV record
     */
    @SuppressWarnings("all")
    public long getPort() {
        return this.port;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "MCPingResponse(description=" + this.getDescription() + ", players=" + this.getPlayers() + ", version=" + this.getVersion() + ", favicon=" + this.getFavicon() + ", ping=" + this.getPing() + ", hostname=" + this.getHostname() + ", port=" + this.getPort() + ")";
    }

    /**
     */
    @SuppressWarnings("all")
    void setPing(final long ping) {
        this.ping = ping;
    }

    /**
     */
    @SuppressWarnings("all")
    void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    /**
     */
    @SuppressWarnings("all")
    void setPort(final long port) {
        this.port = port;
    }
    //</editor-fold>
}
