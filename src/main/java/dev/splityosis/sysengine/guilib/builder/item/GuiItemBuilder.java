package dev.splityosis.sysengine.guilib.builder.item;

import dev.splityosis.sysengine.commandlib.exception.RequirementNotMetException;
import dev.splityosis.sysengine.guilib.builder.requirements.GuiItemRequirement;
import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.events.GuiItemClickEvent;
import dev.splityosis.sysengine.guilib.item.DefaultGuiItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

/**
 * Fluent builder for creating a GuiItem with optional requirements and execution logic.
 */
public class GuiItemBuilder {

    private final Supplier<ItemStack> stackSupplier;
    private final List<GuiItemRequirement<?>> requirements = new ArrayList<>();
    private GuiItemConsumer executor;

    private GuiItemBuilder(Supplier<ItemStack> stackSupplier) {
        this.stackSupplier = Objects.requireNonNull(stackSupplier, "Stack supplier cannot be null");
    }

    /**
     * Create a builder from a fixed ItemStack.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull GuiItemBuilder of(ItemStack stack) {
        return new GuiItemBuilder(() -> Objects.requireNonNull(stack, "ItemStack cannot be null").clone());
    }

    /**
     * Create a builder from a lazy supplier (evaluated on each open).
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull GuiItemBuilder of(Supplier<ItemStack> stackSupplier) {
        return new GuiItemBuilder(stackSupplier);
    }

    /**
     * Adds a requirement that must pass before execution.
     */
    @Contract(value = "_ -> this")
    public <T> @NotNull GuiItemBuilder requirements(GuiItemRequirement<T> requirement) {
        requirements.add(requirement);
        return this;
    }

    /**
     * Adds multiple requirements at once.
     */
    @Contract(value = "_ -> this")
    public @NotNull GuiItemBuilder requirements(GuiItemRequirement<?>... reqs) {
        for (GuiItemRequirement<?> r : reqs) requirements(r);
        return this;
    }

    /**
     * Defines the action to execute when the item is clicked and all requirements pass.
     */
    @Contract(value = "_ -> this")
    public @NotNull GuiItemBuilder executes(GuiItemConsumer executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Builds the GuiItem instance.
     */
    @Contract(value = "-> new", pure = true)
    public @NotNull GuiItem build() {
        DefaultGuiItem guiItem = new DefaultGuiItem(stackSupplier);
        guiItem.onClick(event -> {
            List<Object> requirementValues = new ArrayList<>();
            for (GuiItemRequirement<?> req : requirements) {
                try {
                    Object res = req.evaluate(event);
                    requirementValues.add(res);
                } catch (RequirementNotMetException ex) {
                    req.onNotMet(event, ex);
                    return;
                }
            }
            GuiClickContext context = new GuiClickContext(requirementValues);
            if (executor != null) executor.accept(event, context);
        });
        return guiItem;
    }

    /**
     * Functional interface for click handlers with requirement context.
     */
    @FunctionalInterface
    public interface GuiItemConsumer {
        void accept(@NotNull GuiItemClickEvent event, @NotNull GuiClickContext context);
    }

    /**
     * Context passed into click executors, holding requirement results by index.
     */
    public static class GuiClickContext {
        private final List<Object> requirementValues;

        private GuiClickContext(List<Object> requirementValues) {
            this.requirementValues = requirementValues != null ? requirementValues : Collections.emptyList();
        }

        /**
         * Retrieves the requirement value at the specified index.
         */
        public Object getRequirementValue(int index) {
            if (index >= 0 && index < requirementValues.size()) {
                return requirementValues.get(index);
            }
            return null;
        }

        /**
         * Retrieves the requirement value at the specified index or returns a default if null or out of bounds.
         */
        public Object getRequirementValueOrDefault(int index, Object defaultValue) {
            Object val = getRequirementValue(index);
            return val == null ? defaultValue : val;
        }

        /**
         * Returns an unmodifiable list of all requirement results in order.
         */
        public List<Object> getRequirementValues() {
            return Collections.unmodifiableList(requirementValues);
        }

        /**
         * Logs all requirement values to stdout.
         */
        public void logRequirementValues() {
            for (Object value : requirementValues) {
                System.out.println("Requirement Value: " + value);
            }
        }
    }
}
