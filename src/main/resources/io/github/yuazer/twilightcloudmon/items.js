"use strict";
var __defProp = Object.defineProperty;
var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
var __getOwnPropNames = Object.getOwnPropertyNames;
var __hasOwnProp = Object.prototype.hasOwnProperty;
var __export = (target, all) => {
  for (var name in all)
    __defProp(target, name, { get: all[name], enumerable: true });
};
var __copyProps = (to, from, except, desc) => {
  if (from && typeof from === "object" || typeof from === "function") {
    for (let key of __getOwnPropNames(from))
      if (!__hasOwnProp.call(to, key) && key !== except)
        __defProp(to, key, { get: () => from[key], enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable });
  }
  return to;
};
var __toCommonJS = (mod) => __copyProps(__defProp({}, "__esModule", { value: true }), mod);
var items_exports = {};
__export(items_exports, {
  Items: () => Items
});
module.exports = __toCommonJS(items_exports);
const Items = {
  anti_constant_energy: {
    name: "anti_constant_energy",
    spritenum: 99999,
    num: 99995,
    gen: 4
  },
  flygonitex: {
    name: "flygonite_x",
    megaStone: "Flygon-Mega-X",
    megaEvolves: "Flygon",
    itemUser: ["Flygon"],
    onTakeItem(item, source) {
      if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
      return true;
    },
    num: -1001,
    gen: 6
  },
  flygonitey: {
    name: "flygonite_y",
    megaStone: "Flygon-Mega-Y",
    megaEvolves: "Flygon",
    itemUser: ["Flygon"],
    onTakeItem(item, source) {
      if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
      return true;
    },
    num: -1002,
    gen: 6
  },
  giratinaite: {
    name: "giratinaite",
    megaStone: "Giratina-Mega",
    megaEvolves: "Giratina",
    itemUser: ["Giratina"],
    onTakeItem(item, source) {
      if (item.megaEvolves === source.baseSpecies.baseSpecies) return false;
      return true;
    },
    num: -1003,
    gen: 6
  }
};