package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

public class ParentNode implements ParentTextNode {
    public static final ParentNode EMPTY = new ParentNode(new TextNode[0]);
    protected final TextNode[] children;

    public ParentNode(TextNode[] children) {
        this.children = children;
    }

    @Override
    public final TextNode[] getChildren() {
        return this.children;
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ParentNode(children);
    }

    @Override
    public final ITextComponent toText(ParserContext context, boolean removeSingleSlash) {
        var compact = context != null && context.get(ParserContext.Key.COMPACT_TEXT) != Boolean.FALSE;

        if (this.children.length == 0) {
            return GeneralUtils.emptyText();
        } else if ((this.children.length == 1 && this.children[0] != null) && compact) {
            var out = this.children[0].toText(context, true);
            if (GeneralUtils.isEmpty(out)) {
                return out;
            }

            return (this.applyFormatting((TextComponentBase) out.createCopy(), context)).setStyle(out.getStyle());
        } else {
            TextComponentBase base = compact ? null : GeneralUtils.emptyText();

            for (int i = 0; i < this.children.length; i++) {
                if (this.children[i] != null) {
                    var child = this.children[i].toText(context, true);

                    if (!GeneralUtils.isEmpty(child)) {
                        if (base == null) {
                            if (child.getStyle().isEmpty()) {
                                base = (TextComponentBase) child.createCopy();
                            } else {
                                base = GeneralUtils.emptyText();
                                base.appendSibling(child);
                            }
                        } else {
                            base.appendSibling(child);
                        }
                    }
                }
            }

            if (base == null || GeneralUtils.isEmpty(base)) {
                return GeneralUtils.emptyText();
            }

            return this.applyFormatting(base, context);
        }
    }

    protected ITextComponent applyFormatting(TextComponentBase out, ParserContext context) { return out; };
}
