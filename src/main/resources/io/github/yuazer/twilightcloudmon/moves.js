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
var moves_exports = {};
__export(moves_exports, {
  Moves: () => Moves
});
module.exports = __toCommonJS(moves_exports);
const Moves = {
  colorfulgrass: {
    num: 5001,
    accuracy: 100,
    basePower: 60,
    category: "Physical",
    name: "Colorful Grass",
    pp: 10,
    priority: 0,
    flags: {
      protect: 1,
      mirror: 1,
      contact: 1
    },
    onHit(target, source) {
      this.field.setTerrain('newgrassterrain');
    },
    secondary: null,
    target: "normal",
    type: "Grass",
    contestType: "Beautiful"
  },
  newgrassterrain: {
    num: 9999,
    accuracy: true,
    basePower: 0,
    category: "Status",
    name: "newgrassterrain",
    pp: 10,
    priority: 0,
    flags: { nonsky: 1, metronome: 1 },
    terrain: "newgrassterrain",
    condition: {
      duration: 5,
      durationCallback(source, effect) {
        if (source?.hasItem("terrainextender")) {
          return 8;
        }
        return 5;
      },
      onFieldStart(field, source, effect) {
        this.effectState.source = source;
        this.add("-fieldstart", "move: Newgrass Terrain");
      },
      onResidualOrder: 5,
      onResidualSubOrder: 2,
      onResidual(pokemon) {
        if (pokemon != this.effectState.source) {
          this.heal(pokemon.baseMaxhp / 16);
        }
      },
      onFieldResidualOrder: 27,
      onFieldResidualSubOrder: 7,
      onFieldEnd() {
        this.add("-fieldend", "move: Newgrass Terrain");
      }
    },
    secondary: null,
    target: "self",
    type: "Grass",
    zMove: { boost: { def: 1 } },
    contestType: "Beautiful"
  },
  finaldragonbreath: {
    num: 5002,
    accuracy: 100,
    basePower: 100,
    category: "Special",
    name: "Final Dragon Breath",
    pp: 10,
    priority: 0,
    flags: { protect: 1, mirror: 1, metronome: 1 },
    secondary: {
      chance: 50,
      status: "par"
    },
    target: "normal",
    type: "Dragon",
    contestType: "Cool"
  },
  solarjudgment: {
    num: 5003,
    accuracy: 100,
    basePower: 120,
    category: "Special",
    name: "Solar Judgment",
    pp: 5,
    priority: 2,
    flags: { protect: 1, mirror: 1, metronome: 1 },
    secondary: null,
    target: "normal",
    type: "Fire",
    contestType: "Beautiful"
  },
};
//# sourceMappingURL=moves.js.map
