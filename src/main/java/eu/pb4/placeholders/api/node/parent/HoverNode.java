package eu.pb4.placeholders.api.node.parent;

import com.github.bsideup.jabel.Desugar;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

public final class HoverNode<T> extends ParentNode {
    private final Action action;
    private final T value;

    public HoverNode(TextNode[] children, Action action, T value) {
        super(children);
        this.action = action;
        this.value = value;
    }

    @Override
    protected ITextComponent applyFormatting(ITextComponent out, ParserContext context) {
        if (this.action == Action.TEXT) {
            return out.setStyle(out.getStyle().setHoverEvent(new HoverEvent(this.action.vanillaType(), ((ParentTextNode) this.value).toText(context, true))));
        } else if (this.action == Action.ENTITY) {
            return out.setStyle(out.getStyle().setHoverEvent(((EntityNodeContent) this.value).toVanilla(context)));
        } else {
            return out.setStyle(out.getStyle().setHoverEvent(((ItemStackNodeContent) this.value).toVanilla(context)));
        }

    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new HoverNode<>(children, this.action, this.value);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        if (this.action == Action.TEXT) {
            return new HoverNode<>(children, Action.TEXT, TextNode.asSingle(parser.parseNodes((TextNode) this.value)));
        }
        return this.copyWith(children);
    }

    public Action action() {
        return this.action;
    }

    public T value() {
        return this.value;
    }

    @Desugar
    public record Action(HoverEvent.Action vanillaType) {
        public static final Action ENTITY = new Action(HoverEvent.Action.SHOW_ENTITY);
        public static final Action ITEM_STACK = new Action(HoverEvent.Action.SHOW_ITEM);
        public static final Action TEXT = new Action(HoverEvent.Action.SHOW_TEXT);
    }

    @Desugar
    public record EntityNodeContent(EntityEntry entity) {
        public HoverEvent toVanilla(ParserContext context) {
            try {
                return entity.newInstance(null).getDisplayName().getStyle().getHoverEvent();
            } catch (Exception ignored) {
                return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("If you're seeing this, my wonderful code to invoke getDisplayName on an EntityEntry failed. Woe."));
            }
        }
    }

    @Desugar
    public record ItemStackNodeContent(ItemStack stack) {
        public HoverEvent toVanilla(ParserContext context) {
            return stack.getTextComponent().getStyle().getHoverEvent();
        }
    }
}
