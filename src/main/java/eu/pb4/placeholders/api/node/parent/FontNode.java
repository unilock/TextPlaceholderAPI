package eu.pb4.placeholders.api.node.parent;

//import api.eu.pb4.placeholders.ParserContext;
//import node.api.eu.pb4.placeholders.TextNode;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.text.ITextComponent;
//
//public final class FontNode extends ParentNode {
//    private final ResourceLocation font;
//
//    public FontNode(TextNode[] children, ResourceLocation font) {
//        super(children);
//        this.font = font;
//    }
//
//    @Override
//    protected ITextComponent applyFormatting(ITextComponent out, ParserContext context) {
//        return out.setStyle(out.getStyle().setFont(font));
//    }
//
//    @Override
//    public ParentTextNode copyWith(TextNode[] children) {
//        return new FontNode(children, this.font);
//    }
//}
