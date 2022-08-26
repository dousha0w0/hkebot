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
import com.google.common.base.Charsets;

/**
 * Storage class for {@link MCPing} options.
 */
public class MCPingOptions {
    private String hostname;
    //<editor-fold defaultstate="collapsed" desc="delombok">
    private String charset;
    private int port;
    private int timeout;
    private int protocolVersion;
    //</editor-fold>

    @SuppressWarnings("all")
    private static String $default$charset() {
        return Charsets.UTF_8.displayName();
    }

    @SuppressWarnings("all")
    private static int $default$port() {
        return 25565;
    }

    @SuppressWarnings("all")
    private static int $default$timeout() {
        return 5000;
    }

    @SuppressWarnings("all")
    private static int $default$protocolVersion() {
        return 4;
    }

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    MCPingOptions(final String hostname, final String charset, final int port, final int timeout, final int protocolVersion) {
        this.hostname = hostname;
        this.charset = charset;
        this.port = port;
        this.timeout = timeout;
        this.protocolVersion = protocolVersion;
    }


    @SuppressWarnings("all")
    public static class MCPingOptionsBuilder {
        @SuppressWarnings("all")
        private String hostname;
        @SuppressWarnings("all")
        private boolean charset$set;
        @SuppressWarnings("all")
        private String charset$value;
        @SuppressWarnings("all")
        private boolean port$set;
        @SuppressWarnings("all")
        private int port$value;
        @SuppressWarnings("all")
        private boolean timeout$set;
        @SuppressWarnings("all")
        private int timeout$value;
        @SuppressWarnings("all")
        private boolean protocolVersion$set;
        @SuppressWarnings("all")
        private int protocolVersion$value;

        @SuppressWarnings("all")
        MCPingOptionsBuilder() {
        }

        @SuppressWarnings("all")
        public MCPingOptions.MCPingOptionsBuilder hostname(final String hostname) {
            this.hostname = hostname;
            return this;
        }

        @SuppressWarnings("all")
        public MCPingOptions.MCPingOptionsBuilder charset(final String charset) {
            this.charset$value = charset;
            charset$set = true;
            return this;
        }

        @SuppressWarnings("all")
        public MCPingOptions.MCPingOptionsBuilder port(final int port) {
            this.port$value = port;
            port$set = true;
            return this;
        }

        @SuppressWarnings("all")
        public MCPingOptions.MCPingOptionsBuilder timeout(final int timeout) {
            this.timeout$value = timeout;
            timeout$set = true;
            return this;
        }

        @SuppressWarnings("all")
        public MCPingOptions.MCPingOptionsBuilder protocolVersion(final int protocolVersion) {
            this.protocolVersion$value = protocolVersion;
            protocolVersion$set = true;
            return this;
        }

        @SuppressWarnings("all")
        public MCPingOptions build() {
            String charset$value = this.charset$value;
            if (!this.charset$set) charset$value = MCPingOptions.$default$charset();
            int port$value = this.port$value;
            if (!this.port$set) port$value = MCPingOptions.$default$port();
            int timeout$value = this.timeout$value;
            if (!this.timeout$set) timeout$value = MCPingOptions.$default$timeout();
            int protocolVersion$value = this.protocolVersion$value;
            if (!this.protocolVersion$set) protocolVersion$value = MCPingOptions.$default$protocolVersion();
            return new MCPingOptions(this.hostname, charset$value, port$value, timeout$value, protocolVersion$value);
        }

        @Override
        @SuppressWarnings("all")
        public String toString() {
            return "MCPingOptions.MCPingOptionsBuilder(hostname=" + this.hostname + ", charset$value=" + this.charset$value + ", port$value=" + this.port$value + ", timeout$value=" + this.timeout$value + ", protocolVersion$value=" + this.protocolVersion$value + ")";
        }
    }

    @SuppressWarnings("all")
    public static MCPingOptions.MCPingOptionsBuilder builder() {
        return new MCPingOptions.MCPingOptionsBuilder();
    }

    @SuppressWarnings("all")
    public String getHostname() {
        return this.hostname;
    }

    @SuppressWarnings("all")
    public String getCharset() {
        return this.charset;
    }

    @SuppressWarnings("all")
    public int getPort() {
        return this.port;
    }

    @SuppressWarnings("all")
    public int getTimeout() {
        return this.timeout;
    }

    @SuppressWarnings("all")
    public int getProtocolVersion() {
        return this.protocolVersion;
    }
    //</editor-fold>
}
