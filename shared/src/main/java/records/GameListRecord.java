package records;

import model.GameData;

import java.util.Collection;

public record GameListRecord(Collection<GameData> games) {
}
