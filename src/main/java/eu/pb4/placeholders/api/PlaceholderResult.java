package eu.pb4.placeholders.api;

import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public final class PlaceholderResult {
    private final ITextComponent text;
    private final String string;
    private final boolean valid;

    private PlaceholderResult(ITextComponent text, String reason) {
        if (text != null) {
            this.text = text;
            this.valid = true;
        } else {
            this.text = new TextComponentString("[" + (reason != null ? reason : "Invalid placeholder!") + "]").setStyle(GeneralUtils.emptyStyle().setColor(TextFormatting.GRAY).setItalic(true));
            this.valid = false;
        }
        this.string = this.text.getUnformattedText();
    }

    /**
     * Returns text component from placeholder
     *
     * @return Text
     */
    public ITextComponent text() {
        return this.text;
    }

    /**
     * Returns text component as String (without formatting) from placeholder
     *
     * @return String
     */
    public String string() {
        return this.string;
    }

    /**
     * Checks if placeholder was valid
     *
     * @return boolean
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Create result for invalid placeholder
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult invalid(String reason) {
        return new PlaceholderResult(null, reason);
    }

    /**
     * Create result for invalid placeholder
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult invalid() {
        return new PlaceholderResult(null, null);
    }

    /**
     * Create result for placeholder with formatting
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult value(ITextComponent text) {
        return new PlaceholderResult(text, null);
    }

    /**
     * Create result for placeholder
     *
     * @return PlaceholderResult
     */
    public static PlaceholderResult value(String text) {
        return new PlaceholderResult(TextParserUtils.formatText(text), null);
    }
}


