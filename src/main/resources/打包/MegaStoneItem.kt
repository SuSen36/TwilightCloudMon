package 打包

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.github.yajatkaul.mega_showdown.item.custom.mega.MegaStone
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

class MegaStoneItem(properties: Properties) : MegaStone(properties), ShowdownIdentifiable {
    override fun showdownId(): String {
        return BuiltInRegistries.ITEM.getKey(this).path.lowercase().replace(Regex("[^a-z0-9]+"), "")
    }
}
