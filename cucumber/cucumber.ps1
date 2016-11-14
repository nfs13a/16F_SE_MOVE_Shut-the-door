$shellA = new-object -com shell.application

$pathA = “\implementation\cs374_anon.zip”

$zipA = $shellA.NameSpace(($PSScriptRoot + $pathA))
foreach($itemA in $zipA.items())
{
$shellA.Namespace(($PSScriptRoot + "\implementation")).copyhere($itemA)
}

$shellB = new-object -com shell.application

$pathB = “\implementation\cs374_f16_anon.zip”

$zipB = $shellB.NameSpace(($PSScriptRoot + $pathB))
foreach($itemB in $zipB.items())
{
$shellB.Namespace(($PSScriptRoot + "\implementation")).copyhere($itemB)
}