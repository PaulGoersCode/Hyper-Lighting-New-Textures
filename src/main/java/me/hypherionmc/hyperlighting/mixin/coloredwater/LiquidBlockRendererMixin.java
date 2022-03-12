package me.hypherionmc.hyperlighting.mixin.coloredwater;

import com.mojang.blaze3d.vertex.VertexConsumer;
import me.hypherionmc.hyperlighting.common.fluids.ColoredWater;
import me.hypherionmc.hyperlighting.util.ModUtils;
import me.hypherionmc.hyperlighting.util.OptiHacks;
import me.hypherionmc.hyperlighting.util.RenderUtils;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Optifine hack around BECAUSE THEY CAN'T STOP FUCKING UP CUSTOM FLUIDS!
 */

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {

    @Shadow private TextureAtlasSprite waterOverlay;

    @Shadow private static boolean isNeighborSameFluid(FluidState p_203186_, FluidState p_203187_) { return false; }
    @Shadow public static boolean shouldRenderFace(BlockAndTintGetter p_203167_, BlockPos p_203168_, FluidState p_203169_, BlockState p_203170_, Direction p_203171_, FluidState p_203172_) { return false; }
    @Shadow private float getHeight(BlockAndTintGetter p_203161_, Fluid p_203162_, BlockPos p_203163_, BlockState p_203164_, FluidState p_203165_) { return 0f; }
    @Shadow private int getLightColor(BlockAndTintGetter pLevel, BlockPos pPos) { return 0; }
    @Shadow private static boolean isFaceOccludedByNeighbor(BlockGetter p_203180_, BlockPos p_203181_, Direction p_203182_, float p_203183_, BlockState p_203184_) { return false; }
    @Shadow private float calculateAverageHeight(BlockAndTintGetter p_203150_, Fluid p_203151_, float p_203152_, float p_203153_, float p_203154_, BlockPos p_203155_) { return 0f; }


    @Inject(at = @At("HEAD"), method = "tesselate", cancellable = true)
    public void tesselate(BlockAndTintGetter pLevel, BlockPos pPos, VertexConsumer pConsumer, BlockState state, FluidState pFluidState, CallbackInfoReturnable<Boolean> cir) {
        // Only apply when fluid is Colored Water and Optifine is present
        if (pFluidState.getType() instanceof ColoredWater coloredWater && OptiHacks.hasOptiFine()) {
            TextureAtlasSprite[] atextureatlassprite = RenderUtils.getFluidTextures(coloredWater);
            int i = ModUtils.fluidColorFromDye(coloredWater.getColor());
            float alpha = (float)(i >> 24 & 255) / 255.0F;
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            BlockState blockstate = pLevel.getBlockState(pPos.relative(Direction.DOWN));
            FluidState fluidstate = blockstate.getFluidState();
            BlockState blockstate1 = pLevel.getBlockState(pPos.relative(Direction.UP));
            FluidState fluidstate1 = blockstate1.getFluidState();
            BlockState blockstate2 = pLevel.getBlockState(pPos.relative(Direction.NORTH));
            FluidState fluidstate2 = blockstate2.getFluidState();
            BlockState blockstate3 = pLevel.getBlockState(pPos.relative(Direction.SOUTH));
            FluidState fluidstate3 = blockstate3.getFluidState();
            BlockState blockstate4 = pLevel.getBlockState(pPos.relative(Direction.WEST));
            FluidState fluidstate4 = blockstate4.getFluidState();
            BlockState blockstate5 = pLevel.getBlockState(pPos.relative(Direction.EAST));
            FluidState fluidstate5 = blockstate5.getFluidState();
            boolean flag1 = !isNeighborSameFluid(pFluidState, fluidstate1);
            boolean flag2 = shouldRenderFace(pLevel, pPos, pFluidState, state, Direction.DOWN, fluidstate) && !isFaceOccludedByNeighbor(pLevel, pPos, Direction.DOWN, 0.8888889F, blockstate);
            boolean flag3 = shouldRenderFace(pLevel, pPos, pFluidState, state, Direction.NORTH, fluidstate2);
            boolean flag4 = shouldRenderFace(pLevel, pPos, pFluidState, state, Direction.SOUTH, fluidstate3);
            boolean flag5 = shouldRenderFace(pLevel, pPos, pFluidState, state, Direction.WEST, fluidstate4);
            boolean flag6 = shouldRenderFace(pLevel, pPos, pFluidState, state, Direction.EAST, fluidstate5);
            if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
                cir.setReturnValue(false);
            } else {
                boolean flag7 = false;
                float f3 = pLevel.getShade(Direction.DOWN, true);
                float f4 = pLevel.getShade(Direction.UP, true);
                float f5 = pLevel.getShade(Direction.NORTH, true);
                float f6 = pLevel.getShade(Direction.WEST, true);
                Fluid fluid = pFluidState.getType();
                float f11 = this.getHeight(pLevel, fluid, pPos, state, pFluidState);
                float f7;
                float f8;
                float f9;
                float f10;
                if (f11 >= 1.0F) {
                    f7 = 1.0F;
                    f8 = 1.0F;
                    f9 = 1.0F;
                    f10 = 1.0F;
                } else {
                    float f12 = this.getHeight(pLevel, fluid, pPos.north(), blockstate2, fluidstate2);
                    float f13 = this.getHeight(pLevel, fluid, pPos.south(), blockstate3, fluidstate3);
                    float f14 = this.getHeight(pLevel, fluid, pPos.east(), blockstate5, fluidstate5);
                    float f15 = this.getHeight(pLevel, fluid, pPos.west(), blockstate4, fluidstate4);
                    f7 = this.calculateAverageHeight(pLevel, fluid, f11, f12, f14, pPos.relative(Direction.NORTH).relative(Direction.EAST));
                    f8 = this.calculateAverageHeight(pLevel, fluid, f11, f12, f15, pPos.relative(Direction.NORTH).relative(Direction.WEST));
                    f9 = this.calculateAverageHeight(pLevel, fluid, f11, f13, f14, pPos.relative(Direction.SOUTH).relative(Direction.EAST));
                    f10 = this.calculateAverageHeight(pLevel, fluid, f11, f13, f15, pPos.relative(Direction.SOUTH).relative(Direction.WEST));
                }

                double d1 = pPos.getX() & 15;
                double d2 = pPos.getY() & 15;
                double d0 = pPos.getZ() & 15;
                float f16 = 0.001F;
                float f17 = flag2 ? 0.001F : 0.0F;

                /* Render Regions Hack */
                if (OptiHacks.isRenderRegions()) {
                    int pj = pPos.getX() >> 4 << 4;
                    int kk = pPos.getY() >> 4 << 4;
                    int ll = pPos.getZ() >> 4 << 4;
                    int ii1 = 8;
                    int j1 = pj >> ii1 << ii1;
                    int k1 = ll >> ii1 << ii1;
                    int l1 = pj - j1;
                    int i2 = ll - k1;
                    d0 += l1;
                    d1 += kk;
                    d2 += i2;
                }

                if (flag1 && !isFaceOccludedByNeighbor(pLevel, pPos, Direction.UP, Math.min(Math.min(f8, f10), Math.min(f9, f7)), blockstate1)) {
                    flag7 = true;
                    f8 -= 0.001F;
                    f10 -= 0.001F;
                    f9 -= 0.001F;
                    f7 -= 0.001F;
                    Vec3 vec3 = pFluidState.getFlow(pLevel, pPos);
                    float f18;
                    float f19;
                    float f20;
                    float f21;
                    float f22;
                    float f23;
                    float f24;
                    float f25;
                    if (vec3.x == 0.0D && vec3.z == 0.0D) {
                        TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
                        f18 = textureatlassprite1.getU(0.0D);
                        f22 = textureatlassprite1.getV(0.0D);
                        f19 = f18;
                        f23 = textureatlassprite1.getV(16.0D);
                        f20 = textureatlassprite1.getU(16.0D);
                        f24 = f23;
                        f21 = f20;
                        f25 = f22;
                    } else {
                        TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
                        float f26 = (float)Mth.atan2(vec3.z, vec3.x) - ((float)Math.PI / 2F);
                        float f27 = Mth.sin(f26) * 0.25F;
                        float f28 = Mth.cos(f26) * 0.25F;
                        float f29 = 8.0F;
                        f18 = textureatlassprite.getU((double)(8.0F + (-f28 - f27) * 16.0F));
                        f22 = textureatlassprite.getV((double)(8.0F + (-f28 + f27) * 16.0F));
                        f19 = textureatlassprite.getU((double)(8.0F + (-f28 + f27) * 16.0F));
                        f23 = textureatlassprite.getV((double)(8.0F + (f28 + f27) * 16.0F));
                        f20 = textureatlassprite.getU((double)(8.0F + (f28 + f27) * 16.0F));
                        f24 = textureatlassprite.getV((double)(8.0F + (f28 - f27) * 16.0F));
                        f21 = textureatlassprite.getU((double)(8.0F + (f28 - f27) * 16.0F));
                        f25 = textureatlassprite.getV((double)(8.0F + (-f28 - f27) * 16.0F));
                    }

                    float f49 = (f18 + f19 + f20 + f21) / 4.0F;
                    float f50 = (f22 + f23 + f24 + f25) / 4.0F;
                    float f51 = (float)atextureatlassprite[0].getWidth() / (atextureatlassprite[0].getU1() - atextureatlassprite[0].getU0());
                    float f52 = (float)atextureatlassprite[0].getHeight() / (atextureatlassprite[0].getV1() - atextureatlassprite[0].getV0());
                    float f53 = 4.0F / Math.max(f52, f51);
                    f18 = Mth.lerp(f53, f18, f49);
                    f19 = Mth.lerp(f53, f19, f49);
                    f20 = Mth.lerp(f53, f20, f49);
                    f21 = Mth.lerp(f53, f21, f49);
                    f22 = Mth.lerp(f53, f22, f50);
                    f23 = Mth.lerp(f53, f23, f50);
                    f24 = Mth.lerp(f53, f24, f50);
                    f25 = Mth.lerp(f53, f25, f50);
                    int j = this.getLightColor(pLevel, pPos);
                    float f30 = f4 * f;
                    float f31 = f4 * f1;
                    float f32 = f4 * f2;

                    this.vertex(pConsumer, d1 + 0.0D, d2 + (double)f8, d0 + 0.0D, f30, f31, f32, alpha, f18, f22, j);
                    this.vertex(pConsumer, d1 + 0.0D, d2 + (double)f10, d0 + 1.0D, f30, f31, f32, alpha, f19, f23, j);
                    this.vertex(pConsumer, d1 + 1.0D, d2 + (double)f9, d0 + 1.0D, f30, f31, f32, alpha, f20, f24, j);
                    this.vertex(pConsumer, d1 + 1.0D, d2 + (double)f7, d0 + 0.0D, f30, f31, f32, alpha, f21, f25, j);
                    if (pFluidState.shouldRenderBackwardUpFace(pLevel, pPos.above())) {
                        this.vertex(pConsumer, d1 + 0.0D, d2 + (double)f8, d0 + 0.0D, f30, f31, f32, alpha, f18, f22, j);
                        this.vertex(pConsumer, d1 + 1.0D, d2 + (double)f7, d0 + 0.0D, f30, f31, f32, alpha, f21, f25, j);
                        this.vertex(pConsumer, d1 + 1.0D, d2 + (double)f9, d0 + 1.0D, f30, f31, f32, alpha, f20, f24, j);
                        this.vertex(pConsumer, d1 + 0.0D, d2 + (double)f10, d0 + 1.0D, f30, f31, f32, alpha, f19, f23, j);
                    }
                }

                if (flag2) {
                    float f40 = atextureatlassprite[0].getU0();
                    float f41 = atextureatlassprite[0].getU1();
                    float f42 = atextureatlassprite[0].getV0();
                    float f43 = atextureatlassprite[0].getV1();
                    int l = this.getLightColor(pLevel, pPos.below());
                    float f46 = f3 * f;
                    float f47 = f3 * f1;
                    float f48 = f3 * f2;

                    this.vertex(pConsumer, d1, d2 + (double)f17, d0 + 1.0D, f46, f47, f48, alpha, f40, f43, l);
                    this.vertex(pConsumer, d1, d2 + (double)f17, d0, f46, f47, f48, alpha, f40, f42, l);
                    this.vertex(pConsumer, d1 + 1.0D, d2 + (double)f17, d0, f46, f47, f48, alpha, f41, f42, l);
                    this.vertex(pConsumer, d1 + 1.0D, d2 + (double)f17, d0 + 1.0D, f46, f47, f48, alpha, f41, f43, l);
                    flag7 = true;
                }

                int k = this.getLightColor(pLevel, pPos);

                for(Direction direction : Direction.Plane.HORIZONTAL) {
                    float f44;
                    float f45;
                    double d3;
                    double d4;
                    double d5;
                    double d6;
                    boolean flag8;
                    switch(direction) {
                        case NORTH:
                            f44 = f8;
                            f45 = f7;
                            d3 = d1;
                            d5 = d1 + 1.0D;
                            d4 = d0 + (double)0.001F;
                            d6 = d0 + (double)0.001F;
                            flag8 = flag3;
                            break;
                        case SOUTH:
                            f44 = f9;
                            f45 = f10;
                            d3 = d1 + 1.0D;
                            d5 = d1;
                            d4 = d0 + 1.0D - (double)0.001F;
                            d6 = d0 + 1.0D - (double)0.001F;
                            flag8 = flag4;
                            break;
                        case WEST:
                            f44 = f10;
                            f45 = f8;
                            d3 = d1 + (double)0.001F;
                            d5 = d1 + (double)0.001F;
                            d4 = d0 + 1.0D;
                            d6 = d0;
                            flag8 = flag5;
                            break;
                        default:
                            f44 = f7;
                            f45 = f9;
                            d3 = d1 + 1.0D - (double)0.001F;
                            d5 = d1 + 1.0D - (double)0.001F;
                            d4 = d0;
                            d6 = d0 + 1.0D;
                            flag8 = flag6;
                    }

                    if (flag8 && !isFaceOccludedByNeighbor(pLevel, pPos, direction, Math.max(f44, f45), pLevel.getBlockState(pPos.relative(direction)))) {
                        flag7 = true;
                        BlockPos blockpos = pPos.relative(direction);
                        TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
                        if (atextureatlassprite[2] != null) {
                            if (pLevel.getBlockState(blockpos).shouldDisplayFluidOverlay(pLevel, blockpos, pFluidState)) {
                                textureatlassprite2 = atextureatlassprite[2];
                            }
                        }

                        float f54 = textureatlassprite2.getU(0.0D);
                        float f55 = textureatlassprite2.getU(8.0D);
                        float f33 = textureatlassprite2.getV((double)((1.0F - f44) * 16.0F * 0.5F));
                        float f34 = textureatlassprite2.getV((double)((1.0F - f45) * 16.0F * 0.5F));
                        float f35 = textureatlassprite2.getV(8.0D);
                        float f36 = direction.getAxis() == Direction.Axis.Z ? f5 : f6;
                        float f37 = f4 * f36 * f;
                        float f38 = f4 * f36 * f1;
                        float f39 = f4 * f36 * f2;

                        this.vertex(pConsumer, d3, d2 + (double)f44, d4, f37, f38, f39, alpha, f54, f33, k);
                        this.vertex(pConsumer, d5, d2 + (double)f45, d6, f37, f38, f39, alpha, f55, f34, k);
                        this.vertex(pConsumer, d5, d2 + (double)f17, d6, f37, f38, f39, alpha, f55, f35, k);
                        this.vertex(pConsumer, d3, d2 + (double)f17, d4, f37, f38, f39, alpha, f54, f35, k);
                        if (textureatlassprite2 != this.waterOverlay) {
                            this.vertex(pConsumer, d3, d2 + (double)f17, d4, f37, f38, f39, alpha, f54, f35, k);
                            this.vertex(pConsumer, d5, d2 + (double)f17, d6, f37, f38, f39, alpha, f55, f35, k);
                            this.vertex(pConsumer, d5, d2 + (double)f45, d6, f37, f38, f39, alpha, f55, f34, k);
                            this.vertex(pConsumer, d3, d2 + (double)f44, d4, f37, f38, f39, alpha, f54, f33, k);
                        }
                    }
                }

                cir.setReturnValue(flag7);
            }
        }
    }

    private void vertex(VertexConsumer pConsumer, double pX, double pY, double pZ, float pRed, float pGreen, float pBlue, float alpha, float pU, float pV, int pPackedLight) {
        pConsumer.vertex(pX, pY, pZ).color(pRed, pGreen, pBlue, alpha).uv(pU, pV).uv2(pPackedLight).normal(0.0F, 1.0F, 0.0F).endVertex();
    }
}
