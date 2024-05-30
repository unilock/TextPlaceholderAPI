package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public final class ColorNode extends ParentNode {
    private final TextFormatting color;

    public ColorNode(TextNode[] children, TextFormatting color) {
        super(children);
        this.color = color;
    }

    @Override
    protected ITextComponent applyFormatting(ITextComponent out, ParserContext context) {
        return out.setStyle(out.getStyle().setColor(this.color));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ColorNode(children, this.color);
    }
}
