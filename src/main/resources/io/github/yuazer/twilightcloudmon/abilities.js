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
var abilities_exports = {};
__export(abilities_exports, {
  Abilities: () => Abilities
});
module.exports = __toCommonJS(abilities_exports);
const Abilities = {
  luckyberry: {
    onEatItem(item, pokemon) {
      if (item.isBerry || item.id === 'berryjuice') {
        pokemon.cureStatus();
        const stats = ['atk', 'def', 'spa', 'spd', 'spe'];
        const randomStat = stats[this.random(stats.length)];
        const boost = {};
        boost[randomStat] = 1;
        this.boost(boost, pokemon, pokemon, null, true);
      }
    },
    onUseItem(item, pokemon) {
      if (item.id === 'berryjuice') {
        pokemon.cureStatus();
        const stats = ['atk', 'def', 'spa', 'spd', 'spe'];
        const randomStat = stats[this.random(stats.length)];
        const boost = {};
        boost[randomStat] = 1;
        this.boost(boost, pokemon, pokemon, null, true);
      }
    },
    name: "Lucky Berry",
    rating: 3,
    num: 301 
  },
  reverseadapt: {
    onStart(pokemon) {
      this.add('-ability', pokemon, 'Reverse Adapt');
    },
    onUpdate(pokemon) {
      const weather = pokemon.effectiveWeather();
      const hasAntiConstantEnergy = pokemon.hasItem('anti_constant_energy');

      if (["raindance", "sunnyday", "sandstorm", "hail"].includes(weather) || hasAntiConstantEnergy) {
        if (!pokemon.volatiles['reversestate']) {
          pokemon.addVolatile('reversestate');
          this.add('-start', pokemon, 'Reverse Adapt');
        }
      } else if (pokemon.volatiles['reversestate']) {
        pokemon.removeVolatile('reversestate');
        this.add('-end', pokemon, 'Reverse Adapt');
      }
    },
    onWeather(target, source, effect) {
      if (effect.id === 'sandstorm' || effect.id === 'hail') {
        return false;
      }
    },
    onDamage(damage, target, source, effect) {
      if (!target.volatiles['reversestate']) return;
      if (effect.id === "psn" || effect.id === "tox") {
        this.heal(target.maxhp/8);
        return false;
      }
    },
    onSetStatus(status, target, source, effect) {
      if (!target.volatiles['reversestate']) return;

      if (status.id === 'frz' || status.id === 'slp') {
        this.add('反适应', '反适应状态免疫冰冻和睡眠！');
        return false;
      }
      if (status.id === "brn"){
        if (target.volatiles['spakt']) {
          return false;
        }else{
        const stats = target.storedStats;
        const boost = {};
        if (stats.spa >= stats.atk) {
          boost.spa = 1;
          this.boost(boost, target);
          this.add('反适应', '反转状态将灼烧转化为特攻提升！');
          target.addVolatile('spakt');
          return false;
        } else {
          boost.atk = 1;
          this.boost(boost, target);
          this.add('反适应', '反转状态将灼烧转化为攻击提升！');
          target.addVolatile('spakt');
          return false;
        }
      }
    }
      if (status.id === "par") {
        this.boost({spe: 1}, target);
        this.add('反适应', '反转状态将麻痹转化为速度提升！');
        return false;
      }
    },
    name: "Reverse Adapt",
    rating: 4,
    num: 302
  },
  cottonfiber: {
    onTryHit(target, source, move) {
      if (target !== source && move.type === "Water") {
        this.add('棉韧纤维特性发动！');
        this.boost({ def: 1, spe: -1 }, target);
        return this.chainModify(0.5);
      }
    },
    name: "Cotton Fiber",
    rating: 3,
    num: 303
  },
   shadowofcalamity: {
      onStart(pokemon) {
        this.field.setWeather("raindance");
        this.boost({ spa: 1 });
        this.add("-ability", pokemon, "Shadow of Calamity");
      },
      onAnyBasePowerPriority: 20,
      onAnyBasePower(basePower, source, target, move) {
        if (target === source || move.category === "Status")
          return;

        if (move.type === "Dark") {
          this.debug("Shadow of Calamity Dark boost");
          return this.chainModify(0.1);
        }
         if (source.hasType(move.type)) {
          this.debug("Shadow of Calamity STAB boost");
          return this.chainModify(0.1);
        }
      },
      flags: {},
      name: "Shadow of Calamity",
      rating: 4.5,
      num: -1001
    },
    buzzingwings: {
      onBasePowerPriority: 23,
      onBasePower(basePower, pokemon, target, move) {
        if (move.type === "Bug" || move.type === "Flying") {
          this.debug("Buzzing Wings boost");
          return this.chainModify(1.2);
        }
      },
      flags: {},
      name: "Buzzing Wings",
      rating: 3.5,
      num: -1002
    },
    crystalheart: {
      onEffectiveness(typeMod, target, type, move) {
        if (move.category === "Status") return;
        
        if (typeMod > 0) {
          this.add('-ability', target, 'Crystal Heart');
          this.add('-message', '触发了心晶特性的抵抗效果！');
          return -1;
        }
        
        if (typeMod === 0) {
          this.add('-ability', target, 'Crystal Heart');
          this.add('-message', '触发了心晶特性的抵抗效果！');
          return -1;
        }
        
        return typeMod;
      },
      flags: {},
      name: "Crystal Heart",
      rating: 5,
      num: -1003
    },
    cosmicairflow: {
      onStart(pokemon) {
        this.add('-ability', pokemon, 'Cosmic Airflow');
        if (!this.field.pseudoWeather.gravity) {
          this.field.addPseudoWeather('gravity');
        }
        if (this.field.pseudoWeather.gravity) {
          this.field.pseudoWeather.gravity.duration = 0; // 0表示永久
        }
      },
      onSwitchIn(pokemon) {
        if (!this.field.pseudoWeather.gravity) {
          this.field.addPseudoWeather('gravity');
          this.field.pseudoWeather.gravity.duration = 0;
        }
      },
      onEnd(pokemon) {
        if (this.field.pseudoWeather.gravity && 
            this.field.pseudoWeather.gravity.source === pokemon) {
          this.field.removePseudoWeather('gravity');
        }
      },
      onWeather() {
        this.field.clearWeather();
      },
      onFieldStart(field, source, effect) {
        for (const i in field.pseudoWeather) {
          if (i !== 'gravity') {
            field.removePseudoWeather(i);
          }
        }
      },
      flags: {},
      name: "Cosmic Airflow",
      rating: 4,
      num: -1004
    },
    ancientsword: {
      onStart(pokemon) {
        if (!pokemon.abilityState.atkBoosted) {
          pokemon.abilityState.atkBoosted = true;
          this.boost({ atk: 1 }, pokemon);
        }
        this.singleEvent("WeatherChange", this.effect, this.effectState, pokemon);
      },
      onWeatherChange(pokemon) {
        if (this.field.isWeather("sunnyday")) {
          pokemon.addVolatile("ancientsword");
        } else if (!pokemon.volatiles["ancientsword"]?.fromBooster) {
          pokemon.removeVolatile("ancientsword");
        }
      },
      onEnd(pokemon) {
        delete pokemon.volatiles["ancientsword"];
        this.add("-end", pokemon, "Ancient Sword", "[silent]");
      },
      condition: {
        noCopy: true,
        onStart(pokemon, source, effect) {
          if (effect?.name === "Booster Energy") {
            this.effectState.fromBooster = true;
            this.add("-activate", pokemon, "ability: Ancient Sword", "[fromitem]");
          } else {
            this.add("-activate", pokemon, "ability: Ancient Sword");
          }
          this.effectState.bestStat = pokemon.getBestStat(false, true);
          this.add("-start", pokemon, "ancientsword" + this.effectState.bestStat);
        },
        onModifyAtkPriority: 5,
        onModifyAtk(atk, pokemon) {
          if (this.effectState.bestStat !== "atk" || pokemon.ignoringAbility())
            return;
          return this.chainModify([5325, 4096]);
        },
        onModifyDefPriority: 6,
        onModifyDef(def, pokemon) {
          if (this.effectState.bestStat !== "def" || pokemon.ignoringAbility())
            return;
          return this.chainModify([5325, 4096]);
        },
        onModifySpAPriority: 5,
        onModifySpA(spa, pokemon) {
          if (this.effectState.bestStat !== "spa" || pokemon.ignoringAbility())
            return;
          return this.chainModify([5325, 4096]);
        },
        onModifySpDPriority: 6,
        onModifySpD(spd, pokemon) {
          if (this.effectState.bestStat !== "spd" || pokemon.ignoringAbility())
            return;
          return this.chainModify([5325, 4096]);
        },
        onModifySpe(spe, pokemon) {
          if (this.effectState.bestStat !== "spe" || pokemon.ignoringAbility())
            return;
          return this.chainModify(1.5);
        },
        onEnd(pokemon) {
          this.add("-end", pokemon, "Ancient Sword");
        }
      },
    name: "Ancient Sword",
    rating: 4,
    num: -1005
    },
};
