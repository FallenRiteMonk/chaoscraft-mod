package com.schematical.chaoscraft.ai.outputs;

import com.schematical.chaoscraft.ChaosCraft;
import com.schematical.chaoscraft.ai.OutputNeuron;
import com.schematical.chaoscraft.events.CCWorldEvent;
import com.schematical.chaoscraft.events.CCWorldEventType;
import com.schematical.chaosnet.model.ChaosNetException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONObject;


/**
 * Created by user1a on 12/10/18.
 */
public class CraftOutput extends OutputNeuron {
    private String recipeId;
    protected IRecipe recipe;
    public void init(){

        for (IRecipe irecipe : CraftingManager.REGISTRY)
        {

            ResourceLocation resourceLocation = irecipe.getRegistryName();
            String key = resourceLocation.getResourceDomain() + ":" + resourceLocation.getResourcePath();
            if(recipeId.equals(key)){
                recipe = irecipe;
            }


        }
        if(recipe == null){
            throw new ChaosNetException("Cant find Recipe: " + recipeId);
        }

    }
    @Override
    public void execute() {
        if(this._lastValue <= .5){
            return;
        }
        if(nNet.entity.getDebug()) {
            //ChaosCraft.logger.info(nNet.entity.getCCNamespace() + " Attempting to Craft: " + recipe.getRegistryName() + " - " + recipe.getRecipeOutput().getDisplayName());
        }
        if(!nNet.entity.canCraft(recipe)){
            return;
        }
        ChaosCraft.logger.info("Attempting to Craft: " + recipe.getRegistryName().toString());
        ItemStack outputStack = nNet.entity.craft(recipe);
        if(outputStack == null){
            throw new ChaosNetException("Something went wrong crafting: " + recipe.getRegistryName().toString());
        }

        CCWorldEvent worldEvent = new CCWorldEvent(CCWorldEventType.CRAFT);
        worldEvent.item = outputStack.getItem();
        nNet.entity.entityFitnessManager.test(worldEvent);
        //TODO: Move this to a GUI thing.
        String message = nNet.entity.getCCNamespace() +" Crafted " + this.toLongString();
        ChaosCraft.chat(message);
    }
    @Override
    public void parseData(JSONObject jsonObject){
        super.parseData(jsonObject);
        recipeId = jsonObject.get("recipeId").toString();

        init();
    }
    public String toLongString(){
        String response = super.toLongString();
        response += " " + this.recipeId;
        return response;

    }
}
