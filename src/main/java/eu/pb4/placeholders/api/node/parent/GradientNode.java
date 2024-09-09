package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextFormatting;

public final class GradientNode extends ParentNode {
    private final GradientProvider gradientProvider;

    public GradientNode(TextNode[] children, GradientProvider gradientBuilder) {
        super(children);
        this.gradientProvider = gradientBuilder;
    }

    @Override
    protected ITextComponent applyFormatting(TextComponentBase out, ParserContext context) {
        return GeneralUtils.toGradient(out, this.gradientProvider);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new GradientNode(children, this.gradientProvider);
    }

    @FunctionalInterface
    public interface GradientProvider {
        TextFormatting getColorAt(int index, int length);
    }
}
