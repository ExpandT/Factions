package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.command.requirement.Requirement;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.ps.PS;

import java.util.Collections;
import java.util.Set;

public class CmdFactionsClaim extends FactionsCommand {
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsSetOne cmdFactionsClaimOne = new CmdFactionsSetOne(true);
	public CmdFactionsSetAuto cmdFactionsClaimAuto = new CmdFactionsSetAuto(true);
	//public CmdFactionsSetFill cmdFactionsClaimFill = new CmdFactionsSetFill(true);
	//public CmdFactionsSetSquare cmdFactionsClaimSquare = new CmdFactionsSetSquare(true);
	//public CmdFactionsSetCircle cmdFactionsClaimCircle = new CmdFactionsSetCircle(true);
	//public CmdFactionsSetAll cmdFactionsClaimAll = new CmdFactionsSetAll(true);

}
