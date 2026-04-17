{
  name: "flygonitey",
  spritenum: 330,
  megaStone: "Flygon-Mega-Y",
  megaEvolves: ["Flygon"],
  itemUser: ["Flygon"],
  onTakeItem(item, source) {
    if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
    return true;
  },
  num: -1002,
  gen: 3,
  isNonstandard: "Past"
}
