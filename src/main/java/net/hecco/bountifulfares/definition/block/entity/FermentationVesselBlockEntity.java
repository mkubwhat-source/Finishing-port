package net.hecco.bountifulfares.definition.block.entity;

import net.hecco.bountifulfares.platform.BFRecipeLookup;
import net.hecco.bountifulfares.BountifulFares;
import net.hecco.bountifulfares.definition.block.custom.FermentationVesselBlock;
import net.hecco.bountifulfares.definition.block.enums.FermentationStage;
import net.hecco.bountifulfares.definition.platform.Services;
import net.hecco.bountifulfares.definition.recipe.FermentationRecipe;
import net.hecco.bountifulfares.registry.content.BFBlockEntities;
import net.hecco.bountifulfares.registry.content.BFSounds;
import net.hecco.bountifulfares.registry.misc.BFRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class FermentationVesselBlockEntity extends BlockEntity implements ImplementedInventory {
    public final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    protected final ContainerData propertyDelegate;
    private int progress = 0;
    private int maxProgress;
    public boolean fermented;
    public int particleColor = 0;
    public FermentationVesselBlockEntity(BlockPos pos, BlockState state) {
        super(BFBlockEntities.FERMENTATION_VESSEL_BLOCK_ENTITY.get(), pos, state);
        this.fermented = false;
        this.propertyDelegate = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FermentationVesselBlockEntity.this.progress;
                    case 1 -> FermentationVesselBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0: FermentationVesselBlockEntity.this.progress = value;
                    case 1: FermentationVesselBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.inventory);
        output.putInt("fermenting.progress", this.progress);
        output.putInt("particleColor", this.particleColor);
    }


    @Override
    public void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, this.inventory);
        this.progress = input.getIntOr("fermenting.progress", 0);
        this.particleColor = input.getIntOr("particleColor", 0);
        super.loadAdditional(input);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setParticleColor(int color) {
        this.particleColor = color;
        setChanged();
    }

    public Optional<Integer> getParticleColor() {
        return this.particleColor == 0 ? Optional.empty() : Optional.of(this.particleColor);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        // lazy failsafe thing, bad artyrian bad
        int max = Services.PLATFORM.get().getIntConfigValue("fermentationTime") * 20;
        return (this.maxProgress == max) ? this.maxProgress : max;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }

    public boolean canInsertItem() {
        return this.getItem(0).isEmpty();
    }

    public void insertItem(ItemStack item) {
        assert this.level != null;
        if (!this.level.isClientSide()) {
            this.setItem(0, item.copyWithCount(1));
            setChanged();
        }
    }

    public void removeItem() {
        assert this.level != null;
        this.setItem(0, Items.AIR.getDefaultInstance());
        setChanged();
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (!world.isClientSide()) {
            if (this.maxProgress != (Services.PLATFORM.get().getIntConfigValue("fermentationTime") * 20)) {
                this.maxProgress = Services.PLATFORM.get().getIntConfigValue("fermentationTime") * 20;
            }
            if (this.progress < this.maxProgress && !this.inventory.get(0).isEmpty()) {
                this.progress++;
                setChanged(world, pos, state);
            }
            if (this.progress >= this.maxProgress && this.inventory.get(0).isEmpty()) {
                this.fermented = false;
                this.progress = 0;
                setChanged(world, pos, state);
            }
            if (!this.fermented && this.progress >= this.maxProgress && !this.inventory.get(0).isEmpty()) {
                this.fermented = true;
                if (state.getValue(FermentationVesselBlock.FERMENTATION_STAGE) != FermentationStage.FERMENTED) {
                    world.playSound(null, pos, BFSounds.FERMENTATION_VESSEL_FERMENT.get(), SoundSource.BLOCKS, 1.0F, 0.8F + world.getRandom().nextFloat() / 3);
                    setChanged(world, pos, state);
                }
            }
            if (this.fermented && state.getValue(FermentationVesselBlock.FERMENTATION_STAGE) != FermentationStage.FERMENTED) {
                world.setBlockAndUpdate(pos, state.setValue(FermentationVesselBlock.FERMENTATION_STAGE, FermentationStage.FERMENTED));
            }
        }
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    public Optional<RecipeHolder<FermentationRecipe>> getCurrentRecipe() {
        Optional<RecipeHolder<FermentationRecipe>> recipe = BFRecipeLookup.getFirstMatch(Objects.requireNonNull(this.getLevel()), BFRecipes.FERMENTING.get(), new SingleRecipeInput(inventory.get(0)));
        return recipe.isEmpty() ? Optional.empty() : recipe;
    }
    public InteractionResult tryExtractItem(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand) {
        if (this.fermented) {
            ItemStack output = getCurrentRecipe().isEmpty() ? null : getCurrentRecipe().get().value().getOutput();
            if (output != null) {
                ItemStackTemplate collectorTemplate = output.getItem().getCraftingRemainder();
                Item collector = collectorTemplate != null ? collectorTemplate.item().value() : null;
                if (collector == null) {
                    FermentationVesselBlock.popResource(world, new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), output);
                    world.setBlockAndUpdate(pos, state.setValue(FermentationVesselBlock.FERMENTATION_STAGE, FermentationStage.EMPTY));
                    removeItem();
                    world.playSound(null, pos, BFSounds.FERMENTATION_VESSEL_EMPTY.get(), SoundSource.BLOCKS, 1.0F, 0.8F + world.getRandom().nextFloat() / 3);
                    this.progress = 0;
                    this.fermented = false;
                    setChanged(world, pos, state);
                    return InteractionResult.SUCCESS;
                } else {
                    if (player.getItemInHand(hand).is(collector)) {
                        world.playSound(null, pos, BFSounds.FERMENTATION_VESSEL_EMPTY.get(), SoundSource.BLOCKS, 1.0F, 0.8F + world.getRandom().nextFloat() / 3);
                        if (!player.isCreative()) {
                            player.getItemInHand(hand).shrink(1);
                        }
                        if (player.getItemInHand(hand).isEmpty() && !player.isCreative()) {
                            player.setItemInHand(hand, new ItemStack(output.getItem()));
                        } else if (!player.getInventory().add(new ItemStack(output.getItem()))) {
                            player.drop(new ItemStack(output.getItem()), false, net.minecraft.util.Prediction.SERVER_ONLY);
                        }
                        world.setBlockAndUpdate(pos, state.setValue(FermentationVesselBlock.FERMENTATION_STAGE, FermentationStage.EMPTY));
                        this.progress = 0;
                        this.fermented = false;
                        removeItem();
                        setChanged(world, pos, state);
                        return InteractionResult.SUCCESS;
                    } else {
                        // Player.displayClientMessage(Component, boolean) was removed in 26.3; its
                        // replacement sendSystemMessage(Component) has no actionBar flag (confirmed
                        // via javap - the actionBar-vs-chat distinction is gone).
                        player.sendSystemMessage(Component.translatable("warning." + BountifulFares.MOD_ID + ".fermentation_vessel." + collector));
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}



