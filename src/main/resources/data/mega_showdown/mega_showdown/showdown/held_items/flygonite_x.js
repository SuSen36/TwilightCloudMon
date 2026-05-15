{
  name: "flygonitex",
  spritenum: 330,
  megaStone: "Flygon-Mega-X",
  megaEvolves: ["Flygon"],
  itemUser: ["Flygon"],
  onTakeItem(item, source) {
    if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
    return true;
  },
  num: -1001,
  gen: 3,
  isNonstandard: "Past"
}
