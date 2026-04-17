{
  name: "Flygonite X",
  spritenum: 330,
  megaStone: "Flygon-Mega-X",
  megaEvolves: "Flygon",
  onTakeItem(item, source) {
    if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
    return true;
  },
  isNonstandard: "Past"
}
