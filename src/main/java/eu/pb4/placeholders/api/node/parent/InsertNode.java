package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

public final class InsertNode extends ParentNode {
    private final TextNode value;

    public InsertNode(TextNode[] children, TextNode value) {
        super(children);
        this.value = value;
    }

    public TextNode value() {
        return this.value;
    }

    @Override
    protected ITextComponent applyFormatting(TextComponentBase out, ParserContext context) {
        return out.setStyle(out.getStyle().setInsertion(value.toText(context, true).getUnformattedText()));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new InsertNode(children, this.value);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return new InsertNode(children, TextNode.asSingle(parser.parseNodes(this.value)));
    }
}
