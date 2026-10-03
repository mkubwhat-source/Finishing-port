package alabaster.hearthandharvest.common.fd.block.state;

import alabaster.hearthandharvest.HearthAndHarvest;
import alabaster.hearthandharvest.Config;
import alabaster.hearthandharvest.common.registry.*;
import alabaster.hearthandharvest.common.fd.FDTags;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum CookingPotSupport implements StringRepresentable
{
	NONE("none"),
	TRAY("tray"),
	HANDLE("handle");

	private final String supportName;

	CookingPotSupport(String name) {
		this.supportName = name;
	}

	@Override
	public String toString() {
		return this.getSerializedName();
	}

	@Override
	public @NonNull String getSerializedName() {
		return this.supportName;
	}
}
