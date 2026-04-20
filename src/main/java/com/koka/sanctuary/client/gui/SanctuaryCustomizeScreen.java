package com.koka.sanctuary.client.gui;

import com.koka.sanctuary.worldgen.MainIslandPreset;
import com.koka.sanctuary.worldgen.SanctuaryBiomeSource;
import com.koka.sanctuary.worldgen.SanctuaryChunkGeneratorSettingsFactory;
import com.koka.sanctuary.worldgen.SanctuaryWorldgen;
import com.koka.sanctuary.worldgen.SanctuaryWorldgenOptions;
import com.koka.sanctuary.worldgen.VerticalityProfile;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public final class SanctuaryCustomizeScreen extends Screen {
	private static final int WIDGET_WIDTH = 310;
	private static final int WIDGET_HEIGHT = 20;
	private static final int ROW_SPACING = 24;

	private final CreateWorldScreen parent;
	private final SanctuaryBiomeSource sanctuaryBiomeSource;
	private final List<RegistryEntry<Biome>> startingBiomeOptions;
	private RegistryEntry<Biome> selectedStarterBiome;
	private MainIslandPreset mainIslandPreset;
	private double sanctuaryZoneScale;
	private double starterIslandScale;
	private double archipelagoSize;
	private double archipelagoSpacing;
	private int vanillaFloatingIslandsStartRadius;
	private boolean deepslateCore;
	private VerticalityProfile verticalityProfile;
	private double monolithMassHeight;
	private double monolithShoulderWidth;
	private int fracturedIslandCount;
	private double fracturedRingRadius;
	private double hollowVoidSize;
	private double hollowRingThickness;
	private int spireCount;
	private double spireHeight;
	private OptionsListWidget optionsList;
	private double optionsScrollY;

	public SanctuaryCustomizeScreen(CreateWorldScreen parent, GeneratorOptionsHolder generatorOptionsHolder) {
		super(Text.translatable("screen.sanctuary.customize.title"));
		this.parent = parent;
		ChunkGenerator chunkGenerator = generatorOptionsHolder.selectedDimensions().getChunkGenerator();
		this.sanctuaryBiomeSource = SanctuaryWorldgen.getSanctuaryBiomeSource(chunkGenerator);
		SanctuaryWorldgenOptions currentOptions = this.sanctuaryBiomeSource.options();
		this.startingBiomeOptions = this.sanctuaryBiomeSource.getStartingBiomeCandidates();
		this.selectedStarterBiome = currentOptions.starterBiome();
		this.mainIslandPreset = currentOptions.mainIslandPreset();
		this.sanctuaryZoneScale = currentOptions.sanctuaryZoneScale();
		this.starterIslandScale = currentOptions.starterIslandScale();
		this.archipelagoSize = currentOptions.archipelagoSize();
		this.archipelagoSpacing = currentOptions.archipelagoSpacing();
		this.vanillaFloatingIslandsStartRadius = currentOptions.effectiveVanillaFloatingIslandsStartRadius();
		this.deepslateCore = currentOptions.deepslateCore();
		this.verticalityProfile = currentOptions.verticalityProfile();
		this.monolithMassHeight = currentOptions.monolithMassHeight();
		this.monolithShoulderWidth = currentOptions.monolithShoulderWidth();
		this.fracturedIslandCount = currentOptions.fracturedIslandCount();
		this.fracturedRingRadius = currentOptions.fracturedRingRadius();
		this.hollowVoidSize = currentOptions.hollowVoidSize();
		this.hollowRingThickness = currentOptions.hollowRingThickness();
		this.spireCount = currentOptions.spireCount();
		this.spireHeight = currentOptions.spireHeight();
	}

	@Override
	protected void init() {
		super.init();
		int listTop = 44;
		int buttonY = this.height - 28;
		int listHeight = Math.max(72, buttonY - listTop - 18);
		this.optionsList = new OptionsListWidget(this.client, this.width, listHeight, listTop);
		this.populateOptionsList();
		this.optionsList.setScrollY(Math.min(this.optionsScrollY, this.optionsList.getMaxScrollY()));
		this.addDrawableChild(this.optionsList);

		int buttonWidth = 150;
		int gap = 10;
		int buttonsLeft = (this.width - (buttonWidth * 2 + gap)) / 2;
		this.addDrawableChild(
			ButtonWidget
				.builder(ScreenTexts.DONE, button -> this.applyAndClose())
				.dimensions(buttonsLeft, buttonY, buttonWidth, WIDGET_HEIGHT)
				.build()
		);
		this.addDrawableChild(
			ButtonWidget
				.builder(ScreenTexts.CANCEL, button -> this.close())
				.dimensions(buttonsLeft + buttonWidth + gap, buttonY, buttonWidth, WIDGET_HEIGHT)
				.build()
		);
	}

	private void populateOptionsList() {
		this.addOption(
			CyclingButtonWidget
				.builder(this::getPresetDisplayName, this.mainIslandPreset)
				.values(Arrays.asList(MainIslandPreset.values()))
				.build(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Text.translatable("screen.sanctuary.customize.main_island_preset"), (button, preset) -> {
					this.mainIslandPreset = preset;
					this.rebuildOptionsList();
				})
		);
		this.addOption(
			CyclingButtonWidget
				.builder(SanctuaryBiomeSource::getBiomeDisplayName, this.selectedStarterBiome)
				.values(this.startingBiomeOptions)
				.build(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Text.translatable("screen.sanctuary.customize.starting_biome"), (button, biome) -> this.selectedStarterBiome = biome)
		);
		this.addOption(
			CyclingButtonWidget
				.builder(value -> value ? ScreenTexts.ON : ScreenTexts.OFF, this.deepslateCore)
				.values(List.of(Boolean.TRUE, Boolean.FALSE))
				.build(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Text.translatable("screen.sanctuary.customize.deepslate_core"), (button, value) -> this.deepslateCore = value)
		);
		this.addOption(
			CyclingButtonWidget
				.builder(this::getVerticalityDisplayName, this.verticalityProfile)
				.values(Arrays.asList(VerticalityProfile.values()))
				.build(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, Text.translatable("screen.sanctuary.customize.verticality_profile"), (button, value) -> this.verticalityProfile = value)
		);
		this.addOption(new NumericSlider(
			Text.translatable("screen.sanctuary.customize.sanctuary_zone_scale"),
			SanctuaryWorldgenOptions.MIN_SANCTUARY_ZONE_SCALE,
			SanctuaryWorldgenOptions.MAX_SANCTUARY_ZONE_SCALE,
			this.sanctuaryZoneScale,
			true,
			value -> this.sanctuaryZoneScale = value
		));
		this.addOption(new NumericSlider(
			Text.translatable("screen.sanctuary.customize.starter_island_scale"),
			SanctuaryWorldgenOptions.MIN_STARTER_ISLAND_SCALE,
			SanctuaryWorldgenOptions.MAX_STARTER_ISLAND_SCALE,
			this.starterIslandScale,
			true,
			value -> this.starterIslandScale = value
		));
		this.addOption(new NumericSlider(
			Text.translatable("screen.sanctuary.customize.archipelago_size"),
			SanctuaryWorldgenOptions.MIN_ARCHIPELAGO_SIZE,
			SanctuaryWorldgenOptions.MAX_ARCHIPELAGO_SIZE,
			this.archipelagoSize,
			true,
			value -> this.archipelagoSize = value
		));
		this.addOption(new NumericSlider(
			Text.translatable("screen.sanctuary.customize.archipelago_spacing"),
			SanctuaryWorldgenOptions.MIN_ARCHIPELAGO_SPACING,
			SanctuaryWorldgenOptions.MAX_ARCHIPELAGO_SPACING,
			this.archipelagoSpacing,
			false,
			value -> this.archipelagoSpacing = value
		));
		this.addOption(new IntegerSlider(
			Text.translatable("screen.sanctuary.customize.vanilla_floating_islands_start_radius"),
			SanctuaryWorldgenOptions.MIN_VANILLA_FLOATING_ISLANDS_START_RADIUS,
			SanctuaryWorldgenOptions.MAX_VANILLA_FLOATING_ISLANDS_START_RADIUS,
			this.vanillaFloatingIslandsStartRadius,
			value -> this.vanillaFloatingIslandsStartRadius = value
		));
		this.addPresetSpecificWidgets();
	}

	private void addPresetSpecificWidgets() {
		switch (this.mainIslandPreset) {
			case MONOLITH -> {
				this.addOption(new NumericSlider(
					Text.translatable("screen.sanctuary.customize.monolith_mass_height"),
					SanctuaryWorldgenOptions.MIN_MONOLITH_MASS_HEIGHT,
					SanctuaryWorldgenOptions.MAX_MONOLITH_MASS_HEIGHT,
					this.monolithMassHeight,
					true,
					value -> this.monolithMassHeight = value
				));
				this.addOption(new NumericSlider(
					Text.translatable("screen.sanctuary.customize.monolith_shoulder_width"),
					SanctuaryWorldgenOptions.MIN_MONOLITH_SHOULDER_WIDTH,
					SanctuaryWorldgenOptions.MAX_MONOLITH_SHOULDER_WIDTH,
					this.monolithShoulderWidth,
					true,
					value -> this.monolithShoulderWidth = value
				));
			}
			case FRACTURED_ARCHIPELAGO -> {
				this.addOption(new IntegerSlider(
					Text.translatable("screen.sanctuary.customize.fractured_island_count"),
					SanctuaryWorldgenOptions.MIN_FRACTURED_ISLAND_COUNT,
					SanctuaryWorldgenOptions.MAX_FRACTURED_ISLAND_COUNT,
					this.fracturedIslandCount,
					value -> this.fracturedIslandCount = value
				));
				this.addOption(new NumericSlider(
					Text.translatable("screen.sanctuary.customize.fractured_ring_radius"),
					SanctuaryWorldgenOptions.MIN_FRACTURED_RING_RADIUS,
					SanctuaryWorldgenOptions.MAX_FRACTURED_RING_RADIUS,
					this.fracturedRingRadius,
					true,
					value -> this.fracturedRingRadius = value
				));
			}
			case HOLLOW_CROWN -> {
				this.addOption(new NumericSlider(
					Text.translatable("screen.sanctuary.customize.hollow_void_size"),
					SanctuaryWorldgenOptions.MIN_HOLLOW_VOID_SIZE,
					SanctuaryWorldgenOptions.MAX_HOLLOW_VOID_SIZE,
					this.hollowVoidSize,
					true,
					value -> this.hollowVoidSize = value
				));
				this.addOption(new NumericSlider(
					Text.translatable("screen.sanctuary.customize.hollow_ring_thickness"),
					SanctuaryWorldgenOptions.MIN_HOLLOW_RING_THICKNESS,
					SanctuaryWorldgenOptions.MAX_HOLLOW_RING_THICKNESS,
					this.hollowRingThickness,
					true,
					value -> this.hollowRingThickness = value
				));
			}
			case SPIRE_GARDEN -> {
				this.addOption(new IntegerSlider(
					Text.translatable("screen.sanctuary.customize.spire_count"),
					SanctuaryWorldgenOptions.MIN_SPIRE_COUNT,
					SanctuaryWorldgenOptions.MAX_SPIRE_COUNT,
					this.spireCount,
					value -> this.spireCount = value
				));
				this.addOption(new NumericSlider(
					Text.translatable("screen.sanctuary.customize.spire_height"),
					SanctuaryWorldgenOptions.MIN_SPIRE_HEIGHT,
					SanctuaryWorldgenOptions.MAX_SPIRE_HEIGHT,
					this.spireHeight,
					true,
					value -> this.spireHeight = value
				));
			}
		}
	}

	private void addOption(ClickableWidget widget) {
		this.optionsList.addOption(new OptionEntry(widget));
	}

	private void rebuildOptionsList() {
		if (this.optionsList != null) {
			this.optionsScrollY = this.optionsList.getScrollY();
		}

		this.clearAndInit();
	}

	@Override
	public void close() {
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderStaticBackground(context, delta);
		super.render(context, mouseX, mouseY, delta);
		int contentLeft = (this.width - WIDGET_WIDTH) / 2;
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 14, 0xFFFFFF);
		context.drawTextWithShadow(
			this.textRenderer,
			Text.translatable("screen.sanctuary.customize.description"),
			contentLeft,
			28,
			0xAFAFAF
		);
		context.drawTextWithShadow(
			this.textRenderer,
			Text.translatable("screen.sanctuary.customize.archipelago_hint"),
			contentLeft,
			this.height - 44,
			0x8F8F8F
		);
	}

	private void renderStaticBackground(DrawContext context, float delta) {
		if (this.client != null && this.client.world == null) {
			this.renderPanoramaBackground(context, delta);
			this.renderDarkening(context);
			return;
		}

		this.renderInGameBackground(context);
	}

	private void applyAndClose() {
		SanctuaryWorldgenOptions updatedOptions = new SanctuaryWorldgenOptions(
			this.selectedStarterBiome,
			this.sanctuaryZoneScale,
			this.starterIslandScale,
			this.archipelagoSize,
			this.archipelagoSpacing,
			SanctuaryWorldgenOptions.DEFAULT_MONOLITH_GAP_SCALE,
			this.vanillaFloatingIslandsStartRadius,
			this.deepslateCore,
				false,
			this.verticalityProfile,
			this.mainIslandPreset,
			this.monolithMassHeight,
			this.monolithShoulderWidth,
			this.fracturedIslandCount,
			this.fracturedRingRadius,
			this.hollowVoidSize,
			this.hollowRingThickness,
			this.spireCount,
			this.spireHeight
		);
		this.parent.getWorldCreator().applyModifier((registryManager, selectedDimensions) -> selectedDimensions.with(
			registryManager,
			SanctuaryChunkGeneratorSettingsFactory.createGenerator(registryManager, selectedDimensions, updatedOptions)
		));
		this.close();
	}

	private Text getPresetDisplayName(MainIslandPreset preset) {
		return Text.translatable("screen.sanctuary.customize.main_island_preset." + preset.asString());
	}

	private Text getVerticalityDisplayName(VerticalityProfile profile) {
		return Text.translatable("screen.sanctuary.customize.verticality_profile." + profile.asString());
	}

	private static final class OptionsListWidget extends ElementListWidget<OptionEntry> {
		private OptionsListWidget(MinecraftClient client, int width, int height, int y) {
			super(client, width, height, y, ROW_SPACING);
			this.centerListVertically = false;
		}

		@Override
		public int getRowWidth() {
			return WIDGET_WIDTH;
		}

		@Override
		protected int getScrollbarX() {
			return this.getRowRight() + 10;
		}

		private void addOption(OptionEntry entry) {
			this.addEntry(entry);
		}
	}

	private static final class OptionEntry extends ElementListWidget.Entry<OptionEntry> {
		private final ClickableWidget widget;
		private final List<ClickableWidget> children;

		private OptionEntry(ClickableWidget widget) {
			this.widget = widget;
			this.children = List.of(widget);
		}

		@Override
		public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float delta) {
			this.widget.setDimensionsAndPosition(this.getContentWidth(), WIDGET_HEIGHT, this.getContentX(), this.getContentY() + 2);
			this.widget.render(context, mouseX, mouseY, delta);
		}

		@Override
		public List<? extends Element> children() {
			return this.children;
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.children;
		}
	}

	private static class NumericSlider extends SliderWidget {
		private final Text label;
		private final double minValue;
		private final double maxValue;
		private final boolean multiplierFormat;
		private final java.util.function.DoubleConsumer onValueChanged;

		private NumericSlider(
			Text label,
			double minValue,
			double maxValue,
			double initialValue,
			boolean multiplierFormat,
			java.util.function.DoubleConsumer onValueChanged
		) {
			super(0, 0, WIDGET_WIDTH, WIDGET_HEIGHT, label, normalize(initialValue, minValue, maxValue));
			this.label = label;
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.multiplierFormat = multiplierFormat;
			this.onValueChanged = onValueChanged;
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(
				Text.empty()
					.append(this.label)
					.append(": ")
					.append(Text.literal(this.formatValue()))
			);
		}

		@Override
		protected void applyValue() {
			this.onValueChanged.accept(this.getNumericValue());
		}

		private double getNumericValue() {
			return this.minValue + this.value * (this.maxValue - this.minValue);
		}

		private String formatValue() {
			double numericValue = this.getNumericValue();
			if (this.multiplierFormat) {
				return String.format(Locale.ROOT, "x%.2f", numericValue);
			}

			return String.format(Locale.ROOT, "%+.2f", numericValue);
		}

		private static double normalize(double value, double minValue, double maxValue) {
			if (maxValue <= minValue) {
				return 0.0D;
			}

			double normalized = (value - minValue) / (maxValue - minValue);
			return Math.max(0.0D, Math.min(1.0D, normalized));
		}
	}

	private static class IntegerSlider extends NumericSlider {
		private final Text label;
		private final int minValue;
		private final int maxValue;
		private final java.util.function.IntConsumer onValueChanged;

		private IntegerSlider(
			Text label,
			int minValue,
			int maxValue,
			int initialValue,
			java.util.function.IntConsumer onValueChanged
		) {
			super(label, minValue, maxValue, initialValue, false, value -> {});
			this.label = label;
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.onValueChanged = onValueChanged;
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(
				Text.empty()
					.append(this.label)
					.append(": ")
					.append(Text.literal(Integer.toString(this.getNumericValue())))
			);
		}

		@Override
		protected void applyValue() {
			int numericValue = this.getNumericValue();
			this.value = NumericSlider.normalize(numericValue, this.minValue, this.maxValue);
			this.onValueChanged.accept(numericValue);
			this.updateMessage();
		}

		protected int getNumericValue() {
			return (int) Math.round(this.minValue + this.value * (this.maxValue - this.minValue));
		}
	}

}
