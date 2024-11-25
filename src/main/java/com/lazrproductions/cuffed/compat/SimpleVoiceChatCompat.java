package com.lazrproductions.cuffed.compat;

import com.lazrproductions.cuffed.CuffedMod;
import com.lazrproductions.cuffed.entity.base.IRestrainableEntity;
import com.lazrproductions.cuffed.restraints.custom.DuckTapeHeadRestraint;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;

@ForgeVoicechatPlugin
public class SimpleVoiceChatCompat implements VoicechatPlugin {
    public static VoicechatApi voicechatApi;
    public static VoicechatServerApi serverApi;


    public static void load() {}

    @Override
    public void initialize(VoicechatApi api) {
        voicechatApi = api;
        CuffedMod.LOGGER.info("initialized CUFFED MOD VOICE CHAT PLUGIN");
    }

    @Override
    public String getPluginId() {
        return CuffedMod.MODID;
   }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
    }


    private void onMicrophonePacket(MicrophonePacketEvent event) {
        VoicechatConnection senderConnection = event.getSenderConnection();
        if (senderConnection == null) {
            return;
        }

        if(senderConnection.getPlayer().getPlayer() instanceof IRestrainableEntity res)
        {
            if(res.getHeadRestraintId().equals(DuckTapeHeadRestraint.ID)) {
                event.cancel();
                CuffedMod.LOGGER.info("attempting to cancel voice packet");
            }
        }
    }
}
