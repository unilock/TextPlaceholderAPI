package eu.pb4.placeholders.api.node;

import com.github.bsideup.jabel.Desugar;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.util.text.ITextComponent;

@Desugar
public record DirectTextNode(ITextComponent text) implements TextNode {
    @Override
    public ITextComponent toText(ParserContext context, boolean removeSingleSlash) {
        return this.text;
    }
}
