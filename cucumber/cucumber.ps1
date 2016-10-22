$shell = new-object -com shell.application

$path = “\implementation\cs374_anon.zip”

$zip = $shell.NameSpace(($PSScriptRoot + $path))
foreach($item in $zip.items())
{
$shell.Namespace(($PSScriptRoot + "\implementation")).copyhere($item)
}