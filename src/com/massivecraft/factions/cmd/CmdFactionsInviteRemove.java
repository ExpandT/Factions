package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsInvitedChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.container.TypeSet;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CmdFactionsInviteRemove extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	public CmdFactionsInviteRemove()
	{
		// Parameters
		this.addParameter(TypeSet.get(TypeMPlayer.get()), "players/all", true);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //	
	
	@Override
	public void perform() throws MassiveException
	{
		Set<MPlayer> mplayers = new HashSet<>();
		boolean all = false;
		
		// Args
		if ("all".equalsIgnoreCase(this.argAt(0)))
		{
			Set<String> ids = msenderFaction.getInvitations().keySet();
			// Doesn't show up if list is empty. Test at home if it worked.
			if (ids == null || ids.isEmpty())
			{
				throw new MassiveException().addMsg("<b>Никто не приглашен в вашу фракцию.");
			}
			all = true;
			
			for (String id : ids)
			{
				mplayers.add(MPlayer.get(id));
			}
		}
		else
		{
			mplayers = this.readArgAt(0);
		}
		
		// MPerm
		if ( ! MPerm.getPermInvite().has(msender, msenderFaction, true)) return;
		
		for (MPlayer mplayer : mplayers)
		{
			// Already member?
			if (mplayer.getFaction() == msenderFaction)
			{
				// Mson
				String command = CmdFactions.get().cmdFactionsKick.getCommandLine(mplayer.getName());
				String tooltip = Txt.parse("Нажмите для <c>%s<i>.", command);
				
				Mson kick = Mson.mson(
					mson("Вы могли бы кикнуть его. ").color(ChatColor.YELLOW),
					mson(ChatColor.RED.toString() + tooltip).tooltip(ChatColor.YELLOW.toString() + tooltip).suggest(command)
				);
				
				// Inform
				msg("%s<i> уже является членом %s<i>.", mplayer.getName(), msenderFaction.getName(msender));
				message(kick);
				continue;
			}
			
			// Already invited?
			boolean isInvited = msenderFaction.isInvited(mplayer);
			
			if (isInvited)
			{
				// Event
				EventFactionsInvitedChange event = new EventFactionsInvitedChange(sender, mplayer, msenderFaction, isInvited);
				event.run();
				if (event.isCancelled()) continue;
				isInvited = event.isNewInvited();
				
				// Inform Player
				mplayer.msg("%s<i> забрал ваше приглашение <h>%s<i>.", msender.describeTo(mplayer, true), msenderFaction.describeTo(mplayer));
				
				// Inform Faction
				if ( ! all)
				{
					msenderFaction.msg("%s<i> забрано %s<i> приглашение.", msender.describeTo(msenderFaction), mplayer.describeTo(msenderFaction));
				}
				
				// Apply
				msenderFaction.uninvite(mplayer);
				
				// If all, we do this at last. So we only do it once.
				if (! all) msenderFaction.changed();
			}
			else
			{
				// Mson
				String command = CmdFactions.get().cmdFactionsInvite.cmdFactionsInviteAdd.getCommandLine(mplayer.getName());
				String tooltip = Txt.parse("Нажмите для <c>%s<i>.", command);
				
				Mson invite = Mson.mson(
					mson("Вы можете пригласить его. ").color(ChatColor.YELLOW),
					mson(ChatColor.GREEN.toString() + tooltip).tooltip(ChatColor.YELLOW.toString() + tooltip).suggest(command)
				);
				
				// Inform
				msg("%s <i>не приглашен в %s<i>.", mplayer.describeTo(msender, true), msenderFaction.describeTo(mplayer));
				message(invite);
			}
		}
		
		// Inform Faction if all
		if (all)
		{
			List<String> names = new ArrayList<>();
			for (MPlayer mplayer : mplayers)
			{
				names.add(mplayer.describeTo(msender, true));
			}
			
			Mson factionsRevokeAll = mson(
				Mson.parse("%s<i> отозвана ", msender.describeTo(msenderFaction)),
				Mson.parse("<i>all <h>%s <i>ожидающие приглашения", mplayers.size()).tooltip(names),
				mson(" от вашей фракции.").color(ChatColor.YELLOW)
			);
			
			msenderFaction.sendMessage(factionsRevokeAll);
			msenderFaction.changed();
		}
	}
	
}
