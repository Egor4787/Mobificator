package mc.egor4787.mobificator.client.suggestions;

import mc.egor4787.mobificator.client.handlers.Attributes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class Suggestions {
    private static final int MAX_SUGGESTIONS = 8;
    private static final int ROW_HEIGHT = 16;

    private record Option(String label, String completion) {
    }

    private record Command(String alias, String name, String description) {
    }

    private static final List<Command> COMMANDS = List.of(
            new Command("a", "attribute", "Work with attributes"),
            new Command("e", "effect", "Work with effects"),
            new Command("n", "name", "Work with names")
    );

    private final LivingEntity target;

    private List<Option> matches = new ArrayList<>();
    private String lastCompletion;
    private int index = -1;

    public Suggestions(LivingEntity target) {
        this.target = target;
    }

    private List<Option> findOptions(String input) {
        String value = input.stripLeading();
        int separator = value.indexOf(" ");

        if (separator < 0) {
            String query = value.toLowerCase(Locale.ROOT);

            return COMMANDS.stream()
                    .filter(c -> query.isEmpty()
                            || c.alias().startsWith(query)
                            || c.name().startsWith(query))
                    .map(c -> new Option(
                            c.alias() + " - " + c.description(),
                            c.alias()
                    ))
                    .toList();
        }

        String command = value.substring(0, separator);
        String query = value.substring(separator + 1);

        String search = query.toLowerCase(Locale.ROOT);

        if (command.equalsIgnoreCase("a")
                || command.equalsIgnoreCase("attribute")) {
            String[] parts = query.split(" ", 2);
            if (parts.length > 1)
                return "reset".startsWith(parts[1].trim().toLowerCase(Locale.ROOT))
                        ? List.of(new Option("reset", command + " " + parts[0] + " reset"))
                        : List.of();

            List<Option> options = new ArrayList<>();

            for (AttributeInstance attribute : Attributes.getInstances(target)) {
                String id = attribute.getAttribute().getRegisteredName();
                String name = id.substring(id.indexOf(':') + 1);

                if (!name.toLowerCase(Locale.ROOT).contains(search)) continue;

                options.add(new Option(
                        name + " " + attribute.getBaseValue(),
                        command + " " + name
                ));
            }

            return options;
        }

        if (command.equalsIgnoreCase("e")
                || command.equalsIgnoreCase("effect")) {
            String[] parts = query.split(" ", 2);
            if (parts.length > 1) {
                if (parts[0].equalsIgnoreCase("clear")) return List.of();
                return Stream.of("clear", "infinite")
                        .filter(s -> s.startsWith(parts[1].trim().toLowerCase(Locale.ROOT)))
                        .map(s -> new Option(s, command + " " + parts[0] + " " + s))
                        .toList();
            }

            return Stream.concat(
                            Stream.of("clear"),
                            BuiltInRegistries.MOB_EFFECT.listElements()
                                    .map(holder -> holder.unwrapKey()
                                            .orElseThrow()
                                            .identifier()
                                            .getPath())
                    )
                    .distinct()
                    .sorted()
                    .filter(name -> name.toLowerCase(Locale.ROOT).contains(search))
                    .map(name -> new Option(name, command + " " + name))
                    .toList();
        }

        return List.of();
    }

    public void render(
            GuiGraphicsExtractor graphics,
            Font font,
            EditBox input
    ) {
        String value = input.getValue();

        List<Option> options = value.equals(lastCompletion) && !matches.isEmpty()
                ? matches
                : findOptions(value);

        int y = input.getY() + input.getHeight() + 4;
        int count = 0;


        for (Option option : options) {
            graphics.text(
                    font,
                    option.label,
                    input.getX() + (input.getWidth() - font.width(option.label)) / 2,
                    y + count * ROW_HEIGHT + 4,
                    0xFFFFFFFF,
                    true
            );

            if (++count >= MAX_SUGGESTIONS) break;
        }
    }

    public void complete(EditBox input) {
        String value = input.getValue();

        if (!value.equals(lastCompletion)) {
            matches = findOptions(value);
            index = -1;
        }

        if (matches.isEmpty()) return;

        index = (index + 1) % matches.size();
        lastCompletion = matches.get(index).completion();
        input.setValue(lastCompletion);
    }

    public void resetCompletion() {
        matches = List.of();
        lastCompletion = null;
        index = -1;
    }
}
