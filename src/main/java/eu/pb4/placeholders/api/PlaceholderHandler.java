package eu.pb4.placeholders.api;


@FunctionalInterface
public interface PlaceholderHandler {
    PlaceholderHandler EMPTY = (ctx, arg) -> PlaceholderResult.invalid();
    PlaceholderResult onPlaceholderRequest(PlaceholderContext context, @javax.annotation.Nullable String argument);
}
