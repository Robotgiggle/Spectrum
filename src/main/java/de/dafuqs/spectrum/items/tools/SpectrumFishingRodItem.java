package de.dafuqs.spectrum.items.tools;

import de.dafuqs.spectrum.compat.gofish.GoFishCompat;
import de.dafuqs.spectrum.entity.entity.BedrockFishingBobberEntity;
import de.dafuqs.spectrum.interfaces.PlayerEntityAccessor;
import de.dafuqs.spectrum.registries.SpectrumEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class SpectrumFishingRodItem extends FishingRodItem {
	
	public SpectrumFishingRodItem(Settings settings) {
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);

		PlayerEntityAccessor playerEntityAccessor = ((PlayerEntityAccessor) user);
		if (playerEntityAccessor.getSpectrumBobber() != null) {
			if (!world.isClient) {
				int damage = playerEntityAccessor.getSpectrumBobber().use(itemStack);
				itemStack.damage(damage, user, (p) -> {
					p.sendToolBreakStatus(hand);
				});
			}
			
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			world.emitGameEvent(user, GameEvent.FISHING_ROD_REEL_IN, user);
		} else {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
			if (!world.isClient) {
				int luckOfTheSeaLevel = EnchantmentHelper.getLuckOfTheSea(itemStack);
				int lureLevel = EnchantmentHelper.getLure(itemStack);
				int exuberanceLevel = EnchantmentHelper.getLevel(SpectrumEnchantments.EXUBERANCE, itemStack);
				boolean foundry = EnchantmentHelper.getLevel(SpectrumEnchantments.FOUNDRY, itemStack) > 0;
				spawnBobber(user, world, luckOfTheSeaLevel, lureLevel, exuberanceLevel, foundry);
			}
			
			user.incrementStat(Stats.USED.getOrCreateStat(this));
			world.emitGameEvent(user, GameEvent.FISHING_ROD_CAST, user);
		}
		
		return TypedActionResult.success(itemStack, world.isClient());
	}
	
	public abstract void spawnBobber(PlayerEntity user, World world, int luckOfTheSeaLevel, int lureLevel, int exuberanceLevel, boolean foundry);
	
	public boolean canFishIn(FluidState fluidState) {
		return fluidState.isIn(FluidTags.WATER);
	}
	
	public boolean shouldAutosmelt(ItemStack itemStack) {
		return EnchantmentHelper.getLevel(SpectrumEnchantments.FOUNDRY, itemStack) > 0 || GoFishCompat.hasDeepfry(itemStack);
	}
	
}