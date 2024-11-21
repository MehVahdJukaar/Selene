package net.mehvahdjukaar.moonlight.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

// Interface that has forge methods for flammable blocks. On fabric some code will call these and register them to fabric flammable registry
public interface IFlammable {

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param state     The current state
     * @param level     The current level
     * @param pos       Block position in level
     * @param direction The direction that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    //burn odds
    int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction);

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param state     The current state
     * @param level     The current level
     * @param pos       Block position in level
     * @param direction The direction that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    //ignite odds
    int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction);
}
