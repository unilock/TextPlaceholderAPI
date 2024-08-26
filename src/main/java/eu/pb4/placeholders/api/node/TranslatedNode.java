package eu.pb4.placeholders.api.node;

import com.github.bsideup.jabel.Desugar;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

@Desugar
public record TranslatedNode(String key, Object[] args) implements TextNode {
    public TranslatedNode(String key) {
        this(key, new Object[0]);
    }

    @Override
    public ITextComponent toText(ParserContext context, boolean removeSingleSlash) {
        var args = new Object[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            args[i] = this.args[i] instanceof TextNode textNode ? textNode.toText(context, true) : this.args[i];
        }

        return new TextComponentTranslation(this.key(), args);
    }
}
