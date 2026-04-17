{
  name: "Flygonite Y",
  spritenum: 330,
  megaStone: "Flygon-Mega-Y",
  megaEvolves: "Flygon",
  onTakeItem(item, source) {
    if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
    return true;
  },
  isNonstandard: "Past"
}
