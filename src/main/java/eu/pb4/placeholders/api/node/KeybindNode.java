package eu.pb4.placeholders.api.node;

import com.github.bsideup.jabel.Desugar;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;

@Desugar
public record KeybindNode(String value) implements TextNode {
    @Override
    public ITextComponent toText(ParserContext context, boolean removeSingleSlash) {
        return new TextComponentKeybind(this.value());
    }
}
