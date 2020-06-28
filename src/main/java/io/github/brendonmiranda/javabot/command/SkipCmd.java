package io.github.brendonmiranda.javabot.command;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import io.github.brendonmiranda.javabot.dto.AudioTrackMessageDTO;
import io.github.brendonmiranda.javabot.listener.AudioSendHandlerImpl;
import io.github.brendonmiranda.javabot.listener.GeneralResultHandler;
import io.github.brendonmiranda.javabot.service.AudioQueueService;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author evelynvieira
 */
@Component
public class SkipCmd extends MusicCmd {

	private static final Logger logger = LoggerFactory.getLogger(SkipCmd.class);

	@Autowired
	private AudioQueueService audioQueueService;

	@Autowired
	private AudioPlayerManager audioPlayerManager;

	public SkipCmd() {
		this.name = "skip";
		this.help = "skips the current song";
	}

	@Override
	public void command(CommandEvent event) {

		Guild guild = getGuild(event);
		AudioSendHandlerImpl audioSendHandler = getAudioSendHandler(guild);

		if (audioSendHandler == null) {
			event.replyError("There is no track playing to skip.");
			return;
		}

		AudioPlayer audioPlayer = getAudioPlayer(audioSendHandler);

		if (audioPlayer.getPlayingTrack() != null) {

			AudioTrackMessageDTO audioTrackMessageDTO = audioQueueService.receive(guild.getName());

			if (audioTrackMessageDTO != null) {

				if (audioPlayer.isPaused())
					audioPlayer.setPaused(false);

				audioPlayer.stopTrack();
				audioPlayerManager.loadItem(audioTrackMessageDTO.getAudioTrackInfoDTO().getIdentifier(),
						new GeneralResultHandler(audioPlayer, guild));
			}
			else {
				event.replyError("Your queue is empty.");
			}

		}
	}

}