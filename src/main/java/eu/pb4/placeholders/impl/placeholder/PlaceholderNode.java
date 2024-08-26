package eu.pb4.placeholders.impl.placeholder;

import com.github.bsideup.jabel.Desugar;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

@Desugar
public record PlaceholderNode(String placeholder, Placeholders.PlaceholderGetter getter, boolean optionalContext, @Nullable String argument) implements TextNode {
    @Override
    public ITextComponent toText(ParserContext context, boolean removeSingleSlash) {
        var ctx = context.get(PlaceholderContext.KEY);
        var handler = getter.getPlaceholder(placeholder, context);
        return (ctx != null || this.optionalContext) && handler != null ? handler.onPlaceholderRequest(ctx, argument).text() : GeneralUtils.emptyText();
    }
}
