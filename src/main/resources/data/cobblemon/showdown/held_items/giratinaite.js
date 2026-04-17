{
  name: "Giratinaite",
  spritenum: 487,
  megaStone: "Giratina-Mega",
  megaEvolves: "Giratina",
  onTakeItem(item, source) {
    if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
    return true;
  },
  isNonstandard: "Past"
}