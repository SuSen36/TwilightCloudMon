{
  name: "giratinaite",
  spritenum: 487,
  megaStone: "Giratina-Mega",
  megaEvolves: ["Giratina"],
  itemUser: ["Giratina"],
  onTakeItem(item, source) {
    if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
    return true;
  },
  num: -999,
  gen: 5,
  isNonstandard: "Past"
}