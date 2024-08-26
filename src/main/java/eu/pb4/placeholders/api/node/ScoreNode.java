package eu.pb4.placeholders.api.node;

import com.github.bsideup.jabel.Desugar;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentScore;

@Desugar
public record ScoreNode(String name, String objective) implements TextNode {
    @Override
    public ITextComponent toText(ParserContext context, boolean removeSingleSlash) {
        return new TextComponentScore(name, objective);
    }
}
