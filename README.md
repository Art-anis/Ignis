Это приложение - мобильная реализация настольной игры "Игнис". Правила: https://hobbygames.by/download/rules/Ignis_rules.pdf
```
class BoardViewModel: ViewModel() {
    //крайние координаты
    var startX = 0
    var startY = 0
    var endX = 5
    var endY = 5
    ...
```
Приложение одноэкранное, использует viewmodel для отслеживания состояния игры. Состояние игры представляет собой statelist тайлов, который содержит один из нескольких вариантов значения: пуст, одна из стихий, или не используется.
```
private val _board = startingBoard.toMutableList().toMutableStateList()
```
```
//состояния тайла
data class TileState(var type: TileType, val coordinates: Pair<Int, Int>)

//типы тайлов
enum class TileType {
    EMPTY,
    DESTROYED,
    WATER,
    FIRE,
    EARTH,
    AIR
}
```

Приложение было создано для изучения работы statelist, его взаимодействия с viewmodel, а также из личного интереса к игре.
