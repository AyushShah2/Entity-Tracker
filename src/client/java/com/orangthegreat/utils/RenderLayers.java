package com.orangthegreat.utils;


import java.util.OptionalDouble;
import java.util.function.Function;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

public class RenderLayers {

    // 3D Render Layers
    public static final RenderLayer.MultiPhase QUADS = RenderLayer.of("quads", 2000, false, true,
            RenderBetterPipelines.QUADS, RenderLayer.MultiPhaseParameters.builder().build(false));

    public static final RenderLayer.MultiPhase TRIS = RenderLayer.of("tris", 786432, false, true,
            RenderBetterPipelines.TRIS, RenderLayer.MultiPhaseParameters.builder().build(false));

    public static final RenderLayer.MultiPhase LINES = RenderLayer.of("lines", 2000, RenderBetterPipelines.LINES,
            RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(1)))
                    .build(false));

    // 2D Render Layers
    public static final RenderLayer.MultiPhase QUADS_GUI = RenderLayer.of("quads_gui", 786432, false, true,
            RenderBetterPipelines.QUADS_GUI, RenderLayer.MultiPhaseParameters.builder().build(false));

    public static final Function<Identifier, RenderLayer> TEXTURES_QUADS_GUI = Util
            .memoize((Function<Identifier, RenderLayer>) (texture -> RenderLayer.of("textured_quads_gui", 1536,
                    false, false, RenderPipelines.FIRE_SCREEN_EFFECT, RenderLayer.MultiPhaseParameters.builder()
                            .texture(new RenderPhase.Texture(texture, TriState.FALSE, false)).build(false))));

    public static final RenderLayer.MultiPhase TRIS_GUI = RenderLayer.of("tris_gui", 786432, false, true,
            RenderBetterPipelines.TRIS_GUI, RenderLayer.MultiPhaseParameters.builder().build(false));

    public static final RenderLayer.MultiPhase LINES_GUI = RenderLayer.of("lines_gui", 786432, false, true,
            RenderBetterPipelines.LINES_GUI, RenderLayer.MultiPhaseParameters.builder()
                    .lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(1))).build(false));

}