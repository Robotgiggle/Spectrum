package de.dafuqs.spectrum.worldgen.features;

import com.mojang.serialization.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.util.*;

public class RandomBlockCheckingPatchFeature extends Feature<RandomBlockCheckingPatchFeatureConfig> {

	public RandomBlockCheckingPatchFeature(Codec<RandomBlockCheckingPatchFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(FeatureContext<RandomBlockCheckingPatchFeatureConfig> context) {
		RandomBlockCheckingPatchFeatureConfig randomPatchFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();

		int placedFeatureCount = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int xzSpreadPlus1 = randomPatchFeatureConfig.xzSpread() + 1;
		int ySpreadPlus1 = randomPatchFeatureConfig.ySpread() + 1;

		for (int l = 0; l < randomPatchFeatureConfig.tries(); ++l) {
			mutable.set(blockPos, random.nextInt(xzSpreadPlus1) - random.nextInt(xzSpreadPlus1), random.nextInt(ySpreadPlus1) - random.nextInt(ySpreadPlus1), random.nextInt(xzSpreadPlus1) - random.nextInt(xzSpreadPlus1));
			if (closeToBlock(structureWorldAccess, mutable, randomPatchFeatureConfig.blockScanRange(), randomPatchFeatureConfig.blocksToCheckFor())) {
				if (randomPatchFeatureConfig.closeToBlockFeature().value().generateUnregistered(structureWorldAccess, context.getGenerator(), random, mutable)) {
					++placedFeatureCount;
				}
			} else {
				if (randomPatchFeatureConfig.fallbackFeature().value().generateUnregistered(structureWorldAccess, context.getGenerator(), random, mutable)) {
					++placedFeatureCount;
				}
			}

		}

		return placedFeatureCount > 0;
	}

	protected boolean closeToBlock(StructureWorldAccess world, BlockPos pos, int searchRange, RegistryEntryList<Block> blocksToSearchFor) {
		for (BlockPos currentPos : BlockPos.iterateOutwards(pos, searchRange, searchRange, searchRange)) {
			if (world.getBlockState(currentPos).isIn(blocksToSearchFor)) {
				return true;
			}
		}
		return false;
	}

}
