/*
 *   COPYRIGHT NOTICE
 *
 *   Copyright (C) 2016, SystemUpdate, <admin@systemupdate.io>.
 *
 *   All rights reserved.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 *   NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 *   OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 *   OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *   Except as contained in this notice, the name of a copyright holder shall not
 *   be used in advertising or otherwise to promote the sale, use or other dealings
 *   in this Software without prior written authorization of the copyright holder.
 */

package org.hcgames.hcfactions.faction.system;

import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.faction.Faction;

import java.util.Map;
import java.util.UUID;

public class WildernessFaction extends Faction implements SystemFaction{

    private final static UUID FACTION_UUID = UUID.fromString("ef1d2c32-a234-4fd8-b116-299221c1ec92");

    public WildernessFaction() {
        super("Wilderness", FACTION_UUID);
    }

    public WildernessFaction(Map<String, Object> map) {
        super(map);
    }

    public WildernessFaction(Document document){
        super(document);
    }

    @Override
    public String getFormattedName(CommandSender sender) {
        return Configuration.relationColourWilderness + getName();
    }

    public static UUID getUUID() {
        return FACTION_UUID;
    }
}
