package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.util.text.ITextComponent;

public record EmptyNode() implements TextNode {
    public static final EmptyNode INSTANCE = new EmptyNode();
    @Override
    public ITextComponent toText(ParserContext context, boolean removeSingleSlash) {
        return GeneralUtils.emptyText();
    }
}
