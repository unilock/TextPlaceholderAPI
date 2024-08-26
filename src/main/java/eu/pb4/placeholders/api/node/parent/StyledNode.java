package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import javax.annotation.Nullable;


public final class StyledNode extends ParentNode {
    private final Style style;

    private final ParentNode hoverValue;
    private final TextNode clickValue;
    private final TextNode insertion;

    public StyledNode(TextNode[] children, Style style, @Nullable ParentNode hoverValue, @Nullable TextNode clickValue, @Nullable TextNode insertion) {
        super(children);
        this.style = style;
        this.hoverValue = hoverValue;
        this.clickValue = clickValue;
        this.insertion = insertion;
    }

    public Style style(ParserContext context) {
        var style = this.style;

        if (hoverValue != null && style.getHoverEvent() != null && style.getHoverEvent().getAction() == HoverEvent.Action.SHOW_TEXT) {
            style = style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this.hoverValue.toText(context, true)));
        }

        if (clickValue != null && style.getClickEvent() != null) {
            style = style.setClickEvent(new ClickEvent(style.getClickEvent().getAction(), this.clickValue.toText(context, true).getUnformattedText()));
        }

        if (insertion != null) {
            style = style.setInsertion(this.insertion.toText(context, true).getUnformattedText());
        }
        return style;
    }


    public Style rawStyle() {
        return this.style;
    }

    @Nullable
    public ParentNode hoverValue() {
        return hoverValue;
    }

    @Nullable
    public TextNode clickValue() {
        return clickValue;
    }

    @Nullable
    public TextNode insertion() {
        return insertion;
    }

    @Override
    protected ITextComponent applyFormatting(ITextComponent out, ParserContext context) {
        return (out.getStyle() == GeneralUtils.emptyStyle() ? out : GeneralUtils.emptyText().appendSibling(out)).setStyle(this.style(context));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new StyledNode(children, this.style, this.hoverValue, this.clickValue, this.insertion);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return new StyledNode(children, this.style,
                this.hoverValue != null ? new ParentNode(parser.parseNodes(this.hoverValue)) : null,
                this.clickValue != null ? TextNode.asSingle(parser.parseNodes(this.clickValue)) : null,
                this.insertion != null ? TextNode.asSingle(parser.parseNodes(this.insertion)) : null);
    }
}
