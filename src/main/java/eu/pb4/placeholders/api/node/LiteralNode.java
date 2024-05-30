package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.impl.textparser.TextParserImpl;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public record LiteralNode(String value) implements TextNode {

    public LiteralNode(StringBuilder builder) {
        this(builder.toString());
    }
    @Override
    public ITextComponent toText(ParserContext context, boolean removeSingleSlash) {
        if (removeSingleSlash) {
            var out = this.value();
            for (var e : TextParserImpl.ESCAPED_CHARS) {
                out = out.replace("\\" + e.left(), e.left());
            }
            return new TextComponentString(out);
        } else {
            return new TextComponentString(this.value());
        }
    }
}
