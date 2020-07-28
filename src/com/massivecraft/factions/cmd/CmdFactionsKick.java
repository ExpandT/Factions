package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.IdUtil;
import org.bukkit.ChatColor;

public class CmdFactionsKick extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsKick()
	{
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Arg
		MPlayer mplayer = this.readArg();
		
		// Validate
		if (msender == mplayer)
		{
			msg("<b>Вы не можете кикнуть себя.");
			message(mson(mson("Вы можете захотеть: ").color(ChatColor.YELLOW), CmdFactions.get().cmdFactionsLeave.getTemplate(false)));
			return;
		}
		
		if (mplayer.getRole() == Rel.LEADER && !msender.isOverriding())
		{
			throw new MassiveException().addMsg("<b>Лидер фракции выгнать невозможно.");
		}
		
		if (mplayer.getRole().isMoreThan(msender.getRole()) && ! msender.isOverriding())
		{
			throw new MassiveException().addMsg("<b>Вы не можете выгнать людей более высокого ранга, чем вы.");
		}
		
		if (mplayer.getRole() == msender.getRole() && ! msender.isOverriding())
		{
			throw new MassiveException().addMsg("<b>Вы не можете выгнать людей того же ранга, что и вы.");
		}

		if ( ! MConf.get().canLeaveWithNegativePower && mplayer.getPower() < 0 && ! msender.isOverriding())
		{
			msg("<b>Вы не можете выгнать этого человека, пока его сила не станет положительной.");
			return;
		}
		
		// MPerm
		Faction mplayerFaction = mplayer.getFaction();
		if ( ! MPerm.getPermKick().has(msender, mplayerFaction, true)) return;

		// Event
		EventFactionsMembershipChange event = new EventFactionsMembershipChange(sender, mplayer, FactionColl.get().getNone(), MembershipChangeReason.KICK);
		event.run();
		if (event.isCancelled()) return;

		// Inform
		mplayerFaction.msg("%s<i> выгнал %s<i> из фракции! :O", msender.describeTo(mplayerFaction, true), mplayer.describeTo(mplayerFaction, true));
		mplayer.msg("%s<i> вас выгнал из фракции %s<i>! :O", msender.describeTo(mplayer, true), mplayerFaction.describeTo(mplayer));
		if (mplayerFaction != msenderFaction)
		{
			msender.msg("<i>Вы выгнали %s<i> из фракции %s<i>!", mplayer.describeTo(msender), mplayerFaction.describeTo(msender));
		}

		if (MConf.get().logFactionKick)
		{
			Factions.get().log(msender.getDisplayName(IdUtil.getConsole()) + " выгнал " + mplayer.getName() + " из фракции " + mplayerFaction.getName());
		}

		// Apply
		if (mplayer.getRole() == Rel.LEADER)
		{
			mplayerFaction.promoteNewLeader();
		}
		mplayerFaction.uninvite(mplayer);
		mplayer.resetFactionData();
	}
	
}
