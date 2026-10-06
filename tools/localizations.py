"""Complete additional language packs. English/Russian are staged by the resource builder.

Keep every key and formatting placeholder identical to en_us; never silently fall back
to English for a missing translation. Names intentionally describe Fortune, not Luck.
"""
import json
from pathlib import Path

FAMILIES = 'fortune looting homeward wild_teleport ore_double hot_pick inversion creeper truce swarm ore_sight hunter bat juggernaut shapeshifter gravity'.split()
GUI = 'wart components base brewing result fuel choose picker no_recipes ready'.split()
MESSAGES = 'home_unsafe form brute_space gravity_up gravity_down teleport_search teleport_unsafe'.split()
# Each pack: stand, crop, brute; 16 effects; 10 GUI labels; 7 messages; item templates.
PACKS = {
    'de_de': (
        ['Chaosbraustand', 'Netheritwarze', 'Koloss'],
        ['Glück', 'Plünderung', 'Heimkehr', 'Zufallsteleportation', 'Doppelte Erze', 'Heiße Spitzhacke', 'Umkehrung', 'Creeperherz', 'Waffenruhe', 'Schwarm', 'Erzsucher', 'Jäger', 'Flügel der Nacht', 'Zerstörer', 'Gestaltwandler', 'Schwerkraft'],
        ['Warze', 'Zutaten', 'Basis', 'Brauen...', 'Ergebnis', 'Brennstoff', 'Wählen', 'Trank wählen', 'Wasser oder trinkbaren Trank einsetzen', 'Fertig'],
        ['Kein sicherer Landeplatz nahe deinem Spawnpunkt', 'Gestalt: %s', 'Die Kolossgestalt braucht mehr Platz', 'Schwerkraft: aufwärts. Springen zum Sinken', 'Schwerkraft: abwärts. Springen zum Steigen', 'Sicheres Teleportziel wird gesucht...', 'Kein sicheres Ziel gefunden. Teleportation abgebrochen'],
        ['Trank: {name}', 'Wurftrank: {name}']),
    'es_es': (
        ['Soporte de alquimia del caos', 'Verruga de netherita', 'Coloso'],
        ['Fortuna', 'Saqueo', 'Regreso a casa', 'Teletransporte aleatorio', 'Minerales dobles', 'Pico ardiente', 'Inversión', 'Corazón de creeper', 'Tregua', 'Enjambre', 'Buscador de minerales', 'Cazador', 'Alas de la noche', 'Destructor', 'Cambiaformas', 'Gravedad'],
        ['Verruga', 'Ingredientes', 'Base', 'Elaborando...', 'Resultado', 'Combustible', 'Elegir', 'Elige una poción', 'Introduce agua o una poción bebible', 'Listo'],
        ['No hay un lugar seguro cerca de tu punto de reaparición', 'Forma: %s', 'La forma de coloso necesita más espacio', 'Gravedad: hacia el cielo. Salta para bajar', 'Gravedad: hacia el suelo. Salta para subir', 'Buscando un destino seguro...', 'No se encontró un destino seguro. Teletransporte cancelado'],
        ['Poción de {name}', 'Poción arrojadiza de {name}']),
    'fr_fr': (
        ['Alambic du chaos', 'Verrue de Netherite', 'Colosse'],
        ['Fortune', 'Butin', 'Retour au foyer', 'Téléportation aléatoire', 'Minerais doublés', 'Pioche brûlante', 'Inversion', 'Cœur de Creeper', 'Trêve', 'Horde', 'Chercheur de minerais', 'Chasseur', 'Ailes de la nuit', 'Destructeur', 'Métamorphe', 'Gravité'],
        ['Verrue', 'Ingrédients', 'Base', 'Préparation...', 'Résultat', 'Combustible', 'Choisir', 'Choisir une potion', 'Insérer de l’eau ou une potion buvable', 'Prêt'],
        ['Aucun endroit sûr près de votre point de réapparition', 'Forme : %s', 'La forme de colosse nécessite plus d’espace', 'Gravité : vers le ciel. Sauter pour descendre', 'Gravité : vers le sol. Sauter pour monter', 'Recherche d’une destination sûre...', 'Aucune destination sûre trouvée. Téléportation annulée'],
        ['Potion de {name}', 'Potion jetable de {name}']),
    'pt_br': (
        ['Suporte de poções do caos', 'Fungo de netherita', 'Brutamontes'],
        ['Fortuna', 'Pilhagem', 'Volta para casa', 'Teletransporte aleatório', 'Minérios em dobro', 'Picareta ardente', 'Inversão', 'Coração de creeper', 'Trégua', 'Horda', 'Buscador de minérios', 'Caçador', 'Asas da noite', 'Destruidor', 'Metamorfo', 'Gravidade'],
        ['Fungo', 'Ingredientes', 'Base', 'Preparando...', 'Resultado', 'Combustível', 'Escolher', 'Escolha uma poção', 'Insira água ou uma poção bebível', 'Pronto'],
        ['Não há lugar seguro perto do seu ponto de renascimento', 'Forma: %s', 'A forma de brutamontes precisa de mais espaço', 'Gravidade: para o céu. Pule para descer', 'Gravidade: para o chão. Pule para subir', 'Procurando um destino seguro...', 'Nenhum destino seguro encontrado. Teletransporte cancelado'],
        ['Poção de {name}', 'Poção arremessável de {name}']),
    'zh_cn': (
        ['混沌酿造台', '下界合金疣', '巨汉'],
        ['时运', '抢夺', '归家', '随机传送', '双倍矿物', '炽热镐', '颠倒', '苦力怕之心', '休战', '怪物群', '矿石探寻', '猎人', '夜之翼', '毁灭者', '变形者', '重力'],
        ['疣', '材料', '基底', '酿造中...', '产物', '燃料', '选择', '选择药水', '放入水瓶或饮用型药水', '完成'],
        ['重生点附近没有安全的落脚处', '形态：%s', '巨汉形态需要更多空间', '重力：向上。按跳跃键下降', '重力：向下。按跳跃键上升', '正在寻找安全的传送位置...', '未找到安全位置。传送已取消'],
        ['{name}药水', '喷溅型{name}药水']),
    'ja_jp': (
        ['混沌の醸造台', 'ネザライトウォート', '巨漢'],
        ['幸運', 'ドロップ増加', '帰還', 'ランダムテレポート', '鉱石倍増', '灼熱のツルハシ', '反転', 'クリーパーの心臓', '休戦', '群れ', '鉱石探知', 'ハンター', '夜の翼', '破壊者', '変身', '重力'],
        ['ウォート', '材料', 'ベース', '醸造中...', '完成品', '燃料', '選択', 'ポーションを選択', '水入り瓶か飲めるポーションを入れてください', '完了'],
        ['リスポーン地点の近くに安全な場所がありません', '姿：%s', '巨漢の姿には広い場所が必要です', '重力：上向き。ジャンプで下降', '重力：下向き。ジャンプで上昇', '安全なテレポート先を探しています...', '安全な場所が見つかりません。テレポートを中止しました'],
        ['{name}のポーション', '{name}のスプラッシュポーション']),
    'ko_kr': (
        ['혼돈의 양조기', '네더라이트 사마귀', '거한'],
        ['행운', '약탈', '귀환', '무작위 순간이동', '광물 두 배', '뜨거운 곡괭이', '반전', '크리퍼의 심장', '휴전', '무리', '광석 탐색', '사냥꾼', '밤의 날개', '파괴자', '변신', '중력'],
        ['사마귀', '재료', '기본', '양조 중...', '결과', '연료', '선택', '물약 선택', '물병이나 마시는 물약을 넣으세요', '완료'],
        ['리스폰 지점 근처에 안전한 장소가 없습니다', '형태: %s', '거한 형태에는 더 넓은 공간이 필요합니다', '중력: 위쪽. 점프하여 내려가기', '중력: 아래쪽. 점프하여 올라가기', '안전한 순간이동 위치를 찾는 중...', '안전한 위치를 찾지 못했습니다. 순간이동 취소'],
        ['{name}의 물약', '투척용 {name}의 물약']),
    'uk_ua': (
        ['Хаотична варильна стійка', 'Незеритовий наріст', 'Здоровило'],
        ['Удача', 'Здобич', 'Повернення додому', 'Випадкова телепортація', 'Подвійна руда', 'Гаряче кайло', 'Перевертання', 'Серце кріпера', 'Перемир’я', 'Орда', 'Шукач руди', 'Мисливець', 'Крила ночі', 'Руйнівник', 'Перевертень', 'Гравітація'],
        ['Наріст', 'Складники', 'Основа', 'Варіння...', 'Результат', 'Паливо', 'Обрати', 'Оберіть зілля', 'Покладіть воду або питне зілля', 'Готово'],
        ['Поблизу точки відродження немає безпечного місця', 'Форма: %s', 'Для форми здоровила потрібно більше місця', 'Гравітація: до неба. Стрибок — униз', 'Гравітація: до землі. Стрибок — угору', 'Шукаємо безпечне місце телепортації...', 'Безпечного місця не знайдено. Телепортацію скасовано'],
        ['Зілля: {name}', 'Вибухове зілля: {name}']),
}

def stage_languages(root: Path):
    folder = root / 'common/src/main/resources/assets/forbidden_brews/lang'
    english = json.loads((folder / 'en_us.json').read_text(encoding='utf-8'))
    for locale, (objects, effects, gui, messages, templates) in PACKS.items():
        assert (len(objects), len(effects), len(gui), len(messages), len(templates)) == (3, 16, 10, 7, 2)
        lang = dict(zip(['block.forbidden_brews.chaos_brewing_stand', 'block.forbidden_brews.netherite_wart', 'entity.forbidden_brews.brute'], objects))
        for family, name in zip(FAMILIES, effects):
            lang['effect.forbidden_brews.' + family] = name
            tiered = family in ['fortune', 'looting', 'inversion']
            for level in range(1, 4 if tiered else 2):
                for splash in [False, True]:
                    label = templates[int(splash)].format(name=name)
                    if tiered:
                        label += ' ' + ['I', 'II', 'III'][level - 1]
                    lang[f'item.forbidden_brews.{family}_{level}_{"splash" if splash else "drink"}'] = label
        lang.update(zip(['gui.forbidden_brews.' + key for key in GUI], gui))
        lang.update(zip(['message.forbidden_brews.' + key for key in MESSAGES], messages))
        validate(lang, english, locale)
        (folder / (locale + '.json')).write_text(json.dumps(lang, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    (folder / 'en_gb.json').write_text(json.dumps(english, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    for path in folder.glob('*.json'):
        validate(json.loads(path.read_text(encoding='utf-8')), english, path.stem)
    print(f'Validated {len(list(folder.glob("*.json")))} complete language packs, {len(english)} keys each')

def validate(lang, english, locale):
    assert lang.keys() == english.keys(), f'{locale}: missing/extra translation keys'
    for key, value in lang.items():
        assert isinstance(value, str) and value.strip(), f'{locale}: empty {key}'
        assert value.count('%s') == english[key].count('%s'), f'{locale}: placeholder mismatch in {key}'

if __name__ == '__main__':
    stage_languages(Path(__file__).resolve().parents[1])
