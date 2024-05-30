package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public final class FormattingNode extends ParentNode {
    private final TextFormatting formatting;

    public FormattingNode(TextNode[] children, TextFormatting formatting) {
        super(children);
        this.formatting = formatting;
    }

    @Override
    protected ITextComponent applyFormatting(ITextComponent out, ParserContext context) {
        // TODO: this will not work well
        return out.setStyle(out.getStyle().setColor(this.formatting));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new FormattingNode(children, this.formatting);
    }
}
