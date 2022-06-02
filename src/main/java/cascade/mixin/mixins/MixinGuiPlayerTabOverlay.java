/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.gui.GuiPlayerTabOverlay
 *  net.minecraft.client.network.NetworkPlayerInfo
 *  net.minecraft.scoreboard.ScorePlayerTeam
 *  net.minecraft.scoreboard.Team
 */
package cascade.mixin.mixins;

import cascade.Cascade;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GuiPlayerTabOverlay.class})
public abstract class MixinGuiPlayerTabOverlay {
    @Inject(method={"getPlayerName"}, at={@At(value="HEAD")}, cancellable=true)
    public void getPlayerNameHook(NetworkPlayerInfo playerInfo, CallbackInfoReturnable<String> info) {
        info.setReturnValue(this.getName(playerInfo));
    }

    String getName(NetworkPlayerInfo info) {
        String name;
        String string = name = info.func_178854_k() != null ? info.func_178854_k().getFormattedText() : ScorePlayerTeam.formatPlayerName((Team)info.func_178850_i(), (String)info.func_178845_a().getName());
        if (Cascade.friendManager.isFriend(name)) {
            return ChatFormatting.AQUA + name;
        }
        return name;
    }
}

