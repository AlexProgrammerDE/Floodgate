/*
 * Copyright (c) 2019-2023 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Floodgate
 */

package org.geysermc.floodgate.core.command.util;

public enum Permission {
    COMMAND_MAIN("floodgate.command.floodgate", PermissionDefault.TRUE),
    COMMAND_MAIN_FIREWALL(COMMAND_MAIN, "firewall", PermissionDefault.OP),
    COMMAND_MAIN_VERSION(COMMAND_MAIN, "version", PermissionDefault.OP),
    COMMAND_LINK("floodgate.command.linkaccount", PermissionDefault.TRUE),
    COMMAND_UNLINK("floodgate.command.unlinkaccount", PermissionDefault.TRUE),
    COMMAND_WHITELIST("floodgate.command.fwhitelist", PermissionDefault.OP),
    COMMAND_LINKED("floodgate.command.linkedaccounts", PermissionDefault.OP),
    COMMAND_LINKED_MANAGE(COMMAND_LINKED, "manage", PermissionDefault.OP),

    NEWS_RECEIVE("floodgate.news.receive", PermissionDefault.OP);

    private final String permission;
    private final PermissionDefault defaultValue;

    Permission(String permission, PermissionDefault defaultValue) {
        this.permission = permission;
        this.defaultValue = defaultValue;
    }

    Permission(Permission parent, String child, PermissionDefault defaultValue) {
        this(parent.get() + "." + child, defaultValue);
    }

    public String get() {
        return permission;
    }

    public PermissionDefault defaultValue() {
        return defaultValue;
    }
}
